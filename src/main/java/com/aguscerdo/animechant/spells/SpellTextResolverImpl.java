package com.aguscerdo.animechant.spells;

import com.aguscerdo.animechant.AnimeChantConfig;
import com.aguscerdo.animechant.TextPreset;
import com.aguscerdo.animechant.presets.PresetLoader;
import com.aguscerdo.animechant.presets.RawPresetData;
import com.aguscerdo.animechant.presets.TextEntry;
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

    private TextPreset currentSpellPreset;

    private ImmutableListMultimap<Spell, TextEntry> presetSpellToText;
    private ImmutableListMultimap<SpellCategory, TextEntry> presetSpellCategoryToText;

    private final Map<Spell, Integer> lastCastTicks = new HashMap<>();
    private final Map<Spell, Integer> lastDisplayTicks = new HashMap<>();

    public SpellTextResolverImpl() {
        currentSpellPreset = TextPreset.NONE;
        presetSpellToText = ImmutableListMultimap.of();
        presetSpellCategoryToText = ImmutableListMultimap.of();
    }

    SpellTextResolverImpl(TextPreset currentSpellPreset,
                          ListMultimap<Spell, TextEntry> spellToText,
                          ListMultimap<SpellCategory, TextEntry> spellCategoryToText) {
        this.currentSpellPreset = currentSpellPreset;
        this.presetSpellToText = ImmutableListMultimap.copyOf(spellToText);
        this.presetSpellCategoryToText = ImmutableListMultimap.copyOf(spellCategoryToText);
    }

    @Override
    public void reloadAll() {
        reloadPresets();

        lastCastTicks.clear();
        lastDisplayTicks.clear();
    }

    @Override
    public  void clear() {
        currentSpellPreset = TextPreset.NONE;

        presetSpellToText = ImmutableListMultimap.of();
        presetSpellCategoryToText = ImmutableListMultimap.of();

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
     * Chooses a random entry from a list
     */
    private static TextEntry randomChoice(List<TextEntry> variants) {
        return variants.size() == 1 ? variants.get(0)
                : variants.get(ThreadLocalRandom.current().nextInt(variants.size()));
    }
}
