package com.animechant;


import com.animechant.enums.Spell;
import com.animechant.enums.SpellCategory;
import com.animechant.enums.SpellOrCategory;
import com.animechant.presets.PresetLoader;
import com.animechant.presets.RawPresetData;
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
    static final ChantResolver EMPTY = new ChantResolver(Map.of(), Map.of());

    @Inject
    private PresetLoader presetLoader;

    @Inject
    private AnimeChantConfig config;

    private Map<Spell, List<String>> spellToText;
    private Map<SpellCategory, List<String>> spellCategoryToText;

    public ChantResolver() {
        spellToText = new LinkedHashMap<>();
        spellCategoryToText = new LinkedHashMap<>();
    }

    ChantResolver(Map<Spell, List<String>> spellToText,
                  Map<SpellCategory, List<String>> spellCategoryToText) {
        this.spellToText = spellToText;
        this.spellCategoryToText = spellCategoryToText;
    }

    public void reload() {
        Optional<RawPresetData> presetData = presetLoader.loadPreset(config.spellPreset());

        if (presetData.isPresent()) {
            spellToText = presetData.get().getSpells();
            spellCategoryToText = presetData.get().getSpellCategories();
        } else {
            spellToText = new LinkedHashMap<>();
            spellCategoryToText = new LinkedHashMap<>();
        }

        // TODO handle user overrides.

        spellToText.remove(Spell.UNKNOWN);
        spellCategoryToText.remove(SpellCategory.NONE);
    }

    /**
     * Resolves a spell to preset texts if found.
     */
    public Optional<Pair<String, SpellOrCategory>> resolveSpell(Spell spell) {
        List<String> variants = spellToText.get(spell);

        if (variants != null && !variants.isEmpty()) {
            return Optional.of(Pair.of(randomChoice(variants), new SpellOrCategory(spell)));
        }

        SpellCategory spellCategory = spell.getSpellCategory();
        if (spellCategory != SpellCategory.NONE) {
            variants = spellCategoryToText.get(spellCategory);
            if (variants != null && !variants.isEmpty()) {
                return Optional.of(Pair.of(randomChoice(variants), new SpellOrCategory(spellCategory)));
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
        return spellToText.isEmpty() && spellCategoryToText.isEmpty();
    }

}
