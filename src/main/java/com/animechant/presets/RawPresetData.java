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

@Value
public class RawPresetData {
    String name;
    ImmutableListMultimap<SpellCategory, String> spellCategories;
    ImmutableListMultimap<Spell, String> spells;

    @JsonCreator
    public RawPresetData(
            @JsonProperty("name") String name,
            @JsonProperty("spell_categories") Map<SpellCategory, List<String>> spellCategories,
            @JsonProperty("spells") Map<Spell, List<String>> spells) {
        this.name = name;
        this.spellCategories = copyOf(spellCategories);
        this.spells = copyOf(spells);
    }

    public RawPresetData(
            @JsonProperty("name") String name,
            ImmutableListMultimap<SpellCategory, String> spellCategories,
            ImmutableListMultimap<Spell, String> spells) {
        this.name = name;
        this.spellCategories = spellCategories;
        this.spells = spells;
    }


    /**
     * Safely convert a Map of List into a ImmutableListMultimap
     */
    private static <K> ImmutableListMultimap<K, String> copyOf(Map<K, List<String>> in) {
        if (in == null || in.isEmpty()) {
            return ImmutableListMultimap.of();
        }

        ImmutableListMultimap.Builder<K, String> builder = ImmutableListMultimap.builder();

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