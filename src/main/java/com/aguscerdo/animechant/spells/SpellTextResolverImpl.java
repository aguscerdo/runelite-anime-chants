package com.aguscerdo.animechant.spells;

import com.aguscerdo.animechant.AnimeChantConfig;
import com.aguscerdo.animechant.chantdata.CustomOverrides;
import com.aguscerdo.animechant.chantdata.PresetLoader;
import com.aguscerdo.animechant.chantdata.model.ChantData;
import com.aguscerdo.animechant.chantdata.model.TextEntry;
import com.aguscerdo.animechant.chantdata.model.ValidationIssue;
import com.aguscerdo.animechant.spells.model.Spell;
import com.aguscerdo.animechant.spells.model.SpellCategory;
import com.aguscerdo.animechant.spells.model.SpellTextPreset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;

/**
 * One layer of text: spell-name entries and category entries. Each entry holds one or
 * more variants; one is picked at random on lookup. Also applies the display throttles.
 */
@Slf4j
@Singleton
public class SpellTextResolverImpl implements SpellTextResolver {
    @Inject
    private Client client;

    @Inject
    private PresetLoader presetLoader;

    @Inject
    private AnimeChantConfig config;

    @Inject
    ScheduledExecutorService executor;

    private SpellTextPreset currentSpellPreset;

    private ImmutableListMultimap<Spell, TextEntry> presetSpellToText;
    private ImmutableListMultimap<Spell, TextEntry> userOverrideSpellToText;

    private ImmutableListMultimap<SpellCategory, TextEntry> presetSpellCategoryToText;
    private ImmutableListMultimap<SpellCategory, TextEntry> userOverrideSpellCategoryToText;

    private ImmutableList<ValidationIssue> overrideValidationIssues;

    private final Map<Spell, Integer> lastCastTicks = new HashMap<>();
    private final Map<Spell, Integer> lastDisplayTicks = new HashMap<>();

    public SpellTextResolverImpl() {
        currentSpellPreset = SpellTextPreset.NONE;
        presetSpellToText = ImmutableListMultimap.of();
        presetSpellCategoryToText = ImmutableListMultimap.of();
    }

    SpellTextResolverImpl(SpellTextPreset currentSpellPreset,
                          ListMultimap<Spell, TextEntry> presetSpellToText,
                          ListMultimap<SpellCategory, TextEntry> presetSpellCategoryToText,
                          ListMultimap<Spell, TextEntry> userSpellToText,
                          ListMultimap<SpellCategory, TextEntry> userSpellCategoryToText

    ) {
        this.currentSpellPreset = currentSpellPreset;

        this.presetSpellToText = ImmutableListMultimap.copyOf(presetSpellToText);
        this.presetSpellCategoryToText = ImmutableListMultimap.copyOf(presetSpellCategoryToText);

        this.userOverrideSpellToText = ImmutableListMultimap.copyOf(userSpellToText);
        this.userOverrideSpellCategoryToText = ImmutableListMultimap.copyOf(userSpellCategoryToText);
    }

    @Override
    public void clear() {
        currentSpellPreset = SpellTextPreset.NONE;

        presetSpellToText = ImmutableListMultimap.of();
        presetSpellCategoryToText = ImmutableListMultimap.of();

        overrideValidationIssues = ImmutableList.of();

        userOverrideSpellToText = ImmutableListMultimap.of();
        userOverrideSpellCategoryToText = ImmutableListMultimap.of();

        lastCastTicks.clear();
        lastDisplayTicks.clear();
    }


    @Override
    public void reloadAll(boolean force) {
        reloadPresets(force);
        reloadOverrides();

        lastCastTicks.clear();
        lastDisplayTicks.clear();
    }


    @Override
    public void reloadPresets(boolean force) {
        if (!force && currentSpellPreset == config.spellPreset()) {
            return;
        }

        currentSpellPreset = config.spellPreset();

        executor.execute(() -> {
            Optional<ChantData> presetData = presetLoader.loadPreset(currentSpellPreset);

            if (presetData.isPresent()) {
                presetSpellToText = ImmutableListMultimap.copyOf(presetData.get().getSpells());
                presetSpellCategoryToText = ImmutableListMultimap.copyOf(presetData.get().getSpellCategories());
            } else {
                presetSpellToText = ImmutableListMultimap.of();
                presetSpellCategoryToText = ImmutableListMultimap.of();
            }
        });
    }

    @Override
    public void reloadOverrides() {
        CustomOverrides.OverridesResult result = CustomOverrides.parseSpellOverrides(config.customSpellOverrides());

        overrideValidationIssues = result.getValidationIssues();
        userOverrideSpellToText = result.getChantData().getSpells();
        userOverrideSpellCategoryToText = result.getChantData().getSpellCategories();
    }


    @Override
    public Optional<TextEntry> resolve(Spell spell) {
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
            log.warn("Resolved text entry has no text or files: {}", spell);
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

        // User Overrides
        List<TextEntry> variants = userOverrideSpellToText.get(spell);
        if (variants != null && !variants.isEmpty()) {
            log.debug("Resolved spell to user override: {}}", spell);
            return Optional.of(randomChoice(variants));
        }

        SpellCategory spellCategory = spell.getSpellCategory();
        if (spellCategory != SpellCategory.UNKNOWN) {
            variants = userOverrideSpellCategoryToText.get(spellCategory);
            if (variants != null && !variants.isEmpty()) {
                log.debug("Resolved spell category to user override: {}", spellCategory);
                return Optional.of(randomChoice(variants));
            }
        }


        variants = presetSpellToText.get(spell);
        if (variants != null && !variants.isEmpty()) {
            log.debug("Resolved spell to preset: {}", spell);
            return Optional.of(randomChoice(variants));
        }

        if (spellCategory != SpellCategory.UNKNOWN) {
            variants = presetSpellCategoryToText.get(spellCategory);
            if (variants != null && !variants.isEmpty()) {
                log.debug("Resolved spell category to preset: {}", spellCategory);
                return Optional.of(randomChoice(variants));
            }
        }

        if (config.fallbackToSpellName()) {
            log.debug("Fallback resolved spell to its name: {}", spell);
            return Optional.of(spell.asTextEntry());
        }

        return Optional.empty();
    }

    /**
     * Chooses a random entry from a list
     */
    private static TextEntry randomChoice(List<TextEntry> variants) {
        return variants.size() == 1 ? variants.get(0)
                : variants.get(ThreadLocalRandom.current().nextInt(variants.size()));
    }
}
