package com.animechant;


import com.animechant.spells.Spell;
import com.animechant.spells.SpellCategory;
import com.animechant.spells.SpellKey;
import com.animechant.presets.PresetLoader;
import com.animechant.presets.RawPresetData;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * One layer of text: spell-name entries and category entries. Each entry holds one or
 * more variants; one is picked at random on lookup.
 */
@Singleton
public class ChantResolver {
    public static final ChantResolver EMPTY = new ChantResolver();

    @Inject
    private PresetLoader presetLoader;

    @Inject
    private AnimeChantConfig config;

    private TextPreset currentSpellPreset;
    private ImmutableListMultimap<Spell, String> presetSpellToText;
    private ImmutableListMultimap<SpellCategory, String> presetSpellCategoryToText;


    public ChantResolver() {
        currentSpellPreset = TextPreset.NONE;
        presetSpellToText = ImmutableListMultimap.of();
        presetSpellCategoryToText = ImmutableListMultimap.of();
    }

    ChantResolver(TextPreset currentSpellPreset,
                  ListMultimap<Spell, String> spellToText,
                  ListMultimap<SpellCategory, String> spellCategoryToText) {
        this.currentSpellPreset = currentSpellPreset;
        this.presetSpellToText = ImmutableListMultimap.copyOf(spellToText);
        this.presetSpellCategoryToText = ImmutableListMultimap.copyOf(spellCategoryToText);
    }

    public void reloadPresets() {
        if (currentSpellPreset == config.spellPreset()) {
            currentSpellPreset = config.spellPreset();
            Optional<RawPresetData> presetData = presetLoader.loadPreset(currentSpellPreset);

            if (presetData.isPresent()) {
                presetSpellToText = ImmutableListMultimap.copyOf(presetData.get().getSpells());
                presetSpellCategoryToText = ImmutableListMultimap.copyOf(presetData.get().getSpellCategories());
            } else {
                presetSpellToText = ImmutableListMultimap.of();
                presetSpellCategoryToText = ImmutableListMultimap.of();
            }
        }

        // TODO handle user overrides.
    }

    /**
     * Resolves a spell to preset texts if found.
     */
    public Optional<Pair<String, SpellKey>> resolveSpell(Spell spell) {
        if (spell.isUnknown()) {
            return Optional.empty();
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

        return Optional.empty();
    }

    /**
     * Chooses a random string from a list
     */
    private static String randomChoice(List<String> variants) {
        return variants.size() == 1 ? variants.get(0)
                : variants.get(ThreadLocalRandom.current().nextInt(variants.size()));
    }

    boolean isEmpty() {
        return presetSpellToText.isEmpty() && presetSpellCategoryToText.isEmpty();
    }

}
