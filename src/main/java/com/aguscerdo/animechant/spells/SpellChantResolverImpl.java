package com.aguscerdo.animechant.spells;


import com.aguscerdo.animechant.AnimeChantConfig;
import com.aguscerdo.animechant.TextPreset;
import com.aguscerdo.animechant.presets.PresetLoader;
import com.aguscerdo.animechant.presets.RawPresetData;
import com.aguscerdo.animechant.presets.TextEntry;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.gameval.VarbitID;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * One layer of text: spell-name entries and category entries. Each entry holds one or
 * more variants; one is picked at random on lookup.
 */
@Slf4j
@Singleton
public class SpellChantResolverImpl implements SpellChantResolver {
    private static final Set<MenuAction> NON_INTERRUPTING_ACTIONS = EnumSet.of(
            MenuAction.EXAMINE_OBJECT, MenuAction.EXAMINE_NPC,
            MenuAction.EXAMINE_ITEM_GROUND,
            MenuAction.EXAMINE_WORLD_ENTITY,
            MenuAction.RUNELITE, MenuAction.RUNELITE_HIGH_PRIORITY,
            MenuAction.RUNELITE_LOW_PRIORITY, MenuAction.RUNELITE_OVERLAY,
            MenuAction.RUNELITE_OVERLAY_CONFIG, MenuAction.RUNELITE_INFOBOX,
            MenuAction.RUNELITE_PLAYER, MenuAction.RUNELITE_WIDGET
    );

    /**
     * A spell the player has clicked that hasn't been launched (animated) yet.
     */
    private static final class PendingCast {
        final Spell spell;
        final int expiresTick;

        PendingCast(Spell spell, int expiresTick) {
            this.spell = spell;
            this.expiresTick = expiresTick;
        }
    }

    @Inject
    private Client client;

    @Inject
    private PresetLoader presetLoader;

    @Inject
    private AnimeChantConfig config;

    private TextPreset currentSpellPreset;

    private ImmutableListMultimap<Spell, TextEntry> presetSpellToText;
    private ImmutableListMultimap<SpellCategory, TextEntry> presetSpellCategoryToText;


    private Optional<PendingCast> pendingCast = Optional.empty();
    private final Map<Spell, Integer> lastCastTicks = new HashMap<>();
    private final Map<Spell, Integer> lastDisplayTicks = new HashMap<>();

    public SpellChantResolverImpl() {
        currentSpellPreset = TextPreset.NONE;
        presetSpellToText = ImmutableListMultimap.of();
        presetSpellCategoryToText = ImmutableListMultimap.of();
    }

    SpellChantResolverImpl(TextPreset currentSpellPreset,
                           ListMultimap<Spell, TextEntry> spellToText,
                           ListMultimap<SpellCategory, TextEntry> spellCategoryToText) {
        this.currentSpellPreset = currentSpellPreset;
        this.presetSpellToText = ImmutableListMultimap.copyOf(spellToText);
        this.presetSpellCategoryToText = ImmutableListMultimap.copyOf(spellCategoryToText);
    }

    @Override
    public void reloadAll() {
        reloadPresets();

        pendingCast = Optional.empty();

        lastCastTicks.clear();
        lastDisplayTicks.clear();
    }

    @Override
    public void reloadPresets() {
        if (currentSpellPreset == config.spellPreset()) {
            return;
        }

        currentSpellPreset = config.spellPreset();
        Optional<RawPresetData> presetData = presetLoader.loadPreset(currentSpellPreset);

        if (presetData.isPresent()) {
            presetSpellToText = ImmutableListMultimap.copyOf(presetData.get().getSpells());
            presetSpellCategoryToText = ImmutableListMultimap.copyOf(presetData.get().getSpellCategories());
        } else {
            presetSpellToText = ImmutableListMultimap.of();
            presetSpellCategoryToText = ImmutableListMultimap.of();
        }

        // TODO handle user overrides.
    }

    @Override
    public void handleManualCastEventIntent(Spell spell, MenuAction action) {
        // Unknown spell, means a click may cancel the pending event.
        if (spell.isUnknown()) {
            if (NON_INTERRUPTING_ACTIONS.contains(action)) return;

            // CANCEL only matters if it's deselecting a targeting spell, not just closing a menu
            if (action == MenuAction.CANCEL && !client.isWidgetSelected()) return;

            // walk, object/NPC/item interaction, widget clicks, etc.
            pendingCast = Optional.empty();
            return;
        }

        // Selecting a targeted spell (WIDGET_TARGET) isn't a cast yet; only the click
        // on the target (WIDGET_TARGET_ON_*) is. Don't arm on the selection click.
        if (action == MenuAction.WIDGET_TARGET) {
            log.debug("Ignoring widget targeted spell {}", spell);

            pendingCast = Optional.empty();
            return;
        }

        pendingCast = Optional.of(new PendingCast(spell, client.getTickCount() + spell.timeoutTicks()));
    }

    @Override
    public Optional<TextEntry> handleManualSpell(int animationId) {
        if (pendingCast.isEmpty()) {
            return Optional.empty();
        }

        PendingCast currentPendingCast = this.pendingCast.get();
        if (!currentPendingCast.spell.animationIdMatches(animationId)) {
            return Optional.empty();
        }

        // Either the spell is correct or expired, clean up either way.
        this.pendingCast = Optional.empty();

        if (client.getTickCount() > currentPendingCast.expiresTick) {
            log.debug("Pending cast expired, ignoring: {} vs {}", client.getTickCount(), currentPendingCast.expiresTick);
            return Optional.empty();
        }

        return processSpellToTextEntry(currentPendingCast.spell);
    }

    @Override
    public Optional<TextEntry> handleAutoCast() {
        int varbit = client.getVarbitValue(VarbitID.AUTOCAST_SPELL);
        if (varbit == 0) {
            return Optional.empty();
        }

        Player me = client.getLocalPlayer();
        if (me == null || me.getInteracting() == null) {
            return Optional.empty();
        }

        log.debug("Autocast animation {} with autocast varbit {}", me.getAnimation(), varbit);

        Spell spell = AutoCastSpell.spellFromAutoCast(varbit);
        if (spell.isUnknown()) {
            log.debug("Unmapped autocast varbit {}", varbit);
            return Optional.empty();
        }

        int animationId = me.getAnimation();
        if (!spell.animationIdMatches(animationId)) {
            log.debug("Animation ID does not match autocast: {} - {}", spell, animationId);
            return Optional.empty();
        }

        return processSpellToTextEntry(spell);
    }


    /**
     * Converts a spell into the text to display IF it should be displayed based on the user configs.
     *
     * @param spell the processed spell to convert to text
     * @return the text to display if any, or empty.
     */
    @VisibleForTesting
    Optional<TextEntry> processSpellToTextEntry(Spell spell) {
        Optional<TextEntry> resolved = resolveSpellText(spell);
        if (resolved.isEmpty()) {
            log.debug("No text for spell `{}`", spell);
            return Optional.empty();
        }

        TextEntry resolvedText = resolved.get();

        int tick = client.getTickCount();

        Integer lastCast = lastCastTicks.put(spell, tick);
        int castDelay = config.spellTextDelayFromLastCastTicks();

        if (castDelay > 0 && lastCast != null && tick - lastCast < castDelay) {
            log.debug("Cast delay is enabled and last cast is too recent, skipping: delay {}; tick {}; last cast {}",
                    castDelay, tick, lastCast);
            return Optional.empty();
        }

        Integer lastDisplay = lastDisplayTicks.getOrDefault(spell, null);
        int displayDelay = config.spellTextDelayTicks();

        if (displayDelay > 0 && lastDisplay != null && tick - lastDisplay < displayDelay) {
            log.debug("Display delay is enabled and last display is too recent, skipping: delay {}; tick {}; last cast {}",
                    displayDelay, tick, lastDisplay);
            return Optional.empty();
        }

        if (resolvedText.getText().isEmpty() && resolvedText.getSound().isEmpty()) {
            log.warn("Resolved text entry has no text or sound: {}", spell);
            return Optional.empty();
        }


        lastDisplayTicks.put(spell, tick);
        return Optional.of(resolvedText);
    }


    /**
     * Resolves a spell to the corresponding text to display, if any.
     */
    private Optional<TextEntry> resolveSpellText(Spell spell) {
        if (spell.isUnknown()) {
            return Optional.empty();
        }

        if (currentSpellPreset == TextPreset.SPELL_NAME) {
            return Optional.of(spell.asTextEntry());
        }

        // TODO handle user overrides
        List<TextEntry> variants = presetSpellToText.get(spell);

        if (variants != null && !variants.isEmpty()) {
            return Optional.of(randomChoice(variants));
        }

        SpellCategory spellCategory = spell.getSpellCategory();
        if (spellCategory != SpellCategory.NONE) {
            variants = presetSpellCategoryToText.get(spellCategory);
            if (variants != null && !variants.isEmpty()) {
                return Optional.of(randomChoice(variants));
            }
        }

        if (config.fallbackToSpellName()) {
            return Optional.of(spell.asTextEntry());
        }

        return Optional.empty();
    }

    /**
     * Chooses a random string from a list
     */
    private static TextEntry randomChoice(List<TextEntry> variants) {
        return variants.size() == 1 ? variants.get(0)
                : variants.get(ThreadLocalRandom.current().nextInt(variants.size()));
    }
}