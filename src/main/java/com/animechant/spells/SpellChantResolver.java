package com.animechant.spells;


import com.animechant.AnimeChantConfig;
import com.animechant.TextPreset;
import com.animechant.presets.PresetLoader;
import com.animechant.presets.RawPresetData;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.gameval.VarbitID;
import org.apache.commons.lang3.tuple.Pair;

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
public class SpellChantResolver {
    @Inject
    private Client client;

    @Inject
    private PresetLoader presetLoader;

    @Inject
    private AnimeChantConfig config;

    private TextPreset currentSpellPreset;
    private ImmutableListMultimap<Spell, String> presetSpellToText;
    private ImmutableListMultimap<SpellCategory, String> presetSpellCategoryToText;

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


    private Optional<SpellChantResolver.PendingCast> pendingCast = Optional.empty();

    private final Map<SpellKey, Integer> lastCastTicks = new HashMap<>();
    private final Map<SpellKey, Integer> lastDisplayTicks = new HashMap<>();

    public SpellChantResolver() {
        currentSpellPreset = TextPreset.NONE;
        presetSpellToText = ImmutableListMultimap.of();
        presetSpellCategoryToText = ImmutableListMultimap.of();
    }

    SpellChantResolver(TextPreset currentSpellPreset,
                       ListMultimap<Spell, String> spellToText,
                       ListMultimap<SpellCategory, String> spellCategoryToText) {
        this.currentSpellPreset = currentSpellPreset;
        this.presetSpellToText = ImmutableListMultimap.copyOf(spellToText);
        this.presetSpellCategoryToText = ImmutableListMultimap.copyOf(spellCategoryToText);
    }

    /** Reloads all the states of the Chant Resolver */
    public void reloadAll() {
        reloadPresets();

        pendingCast = Optional.empty();

        lastCastTicks.clear();
        lastDisplayTicks.clear();
    }

    /** Reload Spell to Text presets if the current config differs from the actual user config */
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


    /**
     * Processes a manual menu event to record, update or clear pending cast intent.
     * <p>
     * If a spell is cast it is stored as a pending spell action to be executed if its animation occurs.
     * If a non-spell breaking action occurs, the pending cast intent is cleared. This is important to aovid mismatches
     * between spells with the same animation.
     *
     * @param spell  the infered spell from menu intents
     * @param action the menu action performed
     */
    public void handleManualCastEventIntent(Spell spell, MenuAction action) {
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

        pendingCast = Optional.of(new PendingCast(spell, client.getTickCount() + spell.timeouteTicks()));
    }

    public Optional<String> handleManualSpell(int animationId) {
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

        return processSpell(currentPendingCast.spell);
    }

    public Optional<String> handleAutocast(Player me) {
        int varbit = client.getVarbitValue(VarbitID.AUTOCAST_SPELL);
        if (varbit == 0 || !(me.getInteracting() instanceof NPC)) {
            return Optional.empty();
        }

        log.debug("Autocast animation {} with autocast varbit {}", me.getAnimation(), varbit);

        Spell spell = AutocastSpell.spellFromAutocast(varbit);
        if (spell.isUnknown()) {
            log.debug("Unmapped autocast varbit {}", varbit);
            return Optional.empty();
        }

        int animationId = me.getAnimation();
        if (!spell.animationIdMatches(animationId)) {
            log.debug("Animation ID does not math autocast: {} - {}", spell, animationId);
            return Optional.empty();
        }

        return processSpell(spell);
    }


    /**
     * Converts a spell into the text to display IF it should be displayed based on the user configs.
     *
     * @param spell the processed spell to convert to text
     * @return the text to display if any, or empty.
     */
    private Optional<String> processSpell(Spell spell) {
        Optional<Pair<String, SpellKey>> resolved = resolveSpellText(spell);
        if (resolved.isEmpty()) {
            log.debug("No text for spell `{}`", spell);
            return Optional.empty();
        }

        String resolvedText = resolved.get().getLeft();
        SpellKey key = resolved.get().getRight();

        int tick = client.getTickCount();

        Integer lastCast = lastCastTicks.put(key, tick);
        int castDelay = config.spellTextDelayFromLastCastTicks();

        if (castDelay > 0 && lastCast != null && tick - lastCast < castDelay) {
            log.debug("Cast delay is enabled and last cast is too recent, skipping: delay {}; tick {}; last cast {}",
                    castDelay, tick, lastCast);
            return Optional.empty();
        }

        Integer lastDisplay = lastDisplayTicks.getOrDefault(key, null);
        int displayDelay = config.spellTextDelayTicks();

        if (displayDelay > 0 && lastDisplay != null && tick - lastDisplay < displayDelay) {
            log.debug("Display delay is enabled and last display is too recent, skipping: delay {}; tick {}; last cast {}",
                    displayDelay, tick, lastDisplay);
            return Optional.empty();
        }

        if (resolvedText.isEmpty()) {
            log.warn("Spell resolved to an empty string, this should not happen: {}", spell);
            return Optional.empty();
        }

        if (config.shoutSpells()) {
            resolvedText += "!";
        }

        lastDisplayTicks.put(key, tick);
        return Optional.of(resolvedText);
    }


    /**
     * Resolves a spell to the corresponding text to display, if any.
     */
    private Optional<Pair<String, SpellKey>> resolveSpellText(Spell spell) {
        if (spell.isUnknown()) {
            return Optional.empty();
        }

        if (currentSpellPreset == TextPreset.SPELL_NAME) {
            return Optional.of(Pair.of(spell.getName(), new SpellKey(spell)));
        }

        // TODO handle user overrides
        List<String> variants = presetSpellToText.get(spell);

        if (variants != null && !variants.isEmpty()) {
            return Optional.of(Pair.of(randomChoice(variants), new SpellKey(spell)));
        }

        SpellCategory spellCategory = spell.getSpellCategory();
        if (spellCategory != SpellCategory.NONE) {
            variants = presetSpellCategoryToText.get(spellCategory);
            if (variants != null && !variants.isEmpty()) {
                return Optional.of(Pair.of(randomChoice(variants), new SpellKey(spellCategory)));
            }
        }

        if (config.fallbackToSpellName()) {
            return Optional.of(Pair.of(spell.getName(), new SpellKey(spell)));
        }

        return Optional.empty();
    }

    /**
     * Chooses a random string from a list
     */
    private static String randomChoice(List<String> variants) {
        return variants.size() == 1 ? variants.get(0)
                : variants.get(ThreadLocalRandom.current().nextInt(variants.size()));
    }
}
