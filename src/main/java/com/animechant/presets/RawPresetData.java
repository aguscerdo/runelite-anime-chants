package com.animechant.presets;

import com.animechant.spells.Spell;
import com.animechant.spells.SpellCategory;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableListMultimap;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Canonical in-memory preset (current schema, version 1). When the schema changes,
 * add a class for the new version and convert older ones into this shape in PresetLoader.
 * <p>
 * Keys are unique in YAML; each key maps to a list of entries, which is why the
 * in-memory form is a multimap (one spell -> many entries, each with its own sound).
 */
@Value
public class RawPresetData {
    public static final int CURRENT_VERSION = 1;

    String name;
    ImmutableListMultimap<SpellCategory, TextEntry> spellCategories;
    ImmutableListMultimap<Spell, TextEntry> spells;

    /**
     * Used by Jackson.
     */
    @JsonCreator
    public RawPresetData(
            @JsonProperty("name") String name,
            @JsonProperty("spell_categories") Map<SpellCategory, List<TextEntry>> spellCategories,
            @JsonProperty("spells") Map<Spell, List<TextEntry>> spells) {
        this.name = name;
        this.spellCategories = copyOf(spellCategories);
        this.spells = copyOf(spells);
    }

    public RawPresetData(
            String name,
            ImmutableListMultimap<SpellCategory, TextEntry> spellCategories,
            ImmutableListMultimap<Spell, TextEntry> spells) {
        this.name = name;
        this.spellCategories = spellCategories;
        this.spells = spells;
    }

    /**
     * Safely convert a Map of List into an ImmutableListMultimap.
     */
    private static <K> ImmutableListMultimap<K, TextEntry> copyOf(
            Map<K, List<TextEntry>> in) {
        if (in == null || in.isEmpty()) {
            return ImmutableListMultimap.of();
        }

        ImmutableListMultimap.Builder<K, TextEntry> builder = ImmutableListMultimap.builder();

        in.forEach((key, values) -> {
            if (key != null && values != null) {
                values.stream()
                        .filter(Objects::nonNull)
                        .forEach(value -> builder.put(key, value));
            }
        });

        return builder.build();
    }
}