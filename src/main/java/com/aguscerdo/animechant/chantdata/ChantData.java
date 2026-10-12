package com.aguscerdo.animechant.chantdata;

import com.aguscerdo.animechant.spells.model.Spell;
import com.aguscerdo.animechant.spells.model.SpellCategory;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableListMultimap;
import lombok.Value;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Canonical in-memory preset (current schema, version 1). When the schema changes,
 * add a class for the new version and convert older ones into this shape in PresetLoader.
 * <p>
 * Keys are unique in YAML; each key maps to a list of entries, which is why the
 * in-memory form is a multimap (one spell -> many entries, each with its own sound).
 */
@Value
public class ChantData {
    public static final int CURRENT_VERSION = 1;

    String name;
    String spellSoundDirectory;

    ImmutableListMultimap<SpellCategory, TextEntry> spellCategories;
    ImmutableListMultimap<Spell, TextEntry> spells;

    public ChantData() {
        name = "";
        spellSoundDirectory = "";
        spellCategories = ImmutableListMultimap.of();
        spells = ImmutableListMultimap.of();
    }

    /**
     * Used by Jackson.
     */
    @JsonCreator
    public ChantData(
            @JsonProperty("name") String name,
            @JsonProperty("spell_sound_directory") String spellSoundDirectory,
            @JsonProperty("spell_categories") Map<SpellCategory, List<TextEntry>> spellCategories,
            @JsonProperty("spells") Map<Spell, List<TextEntry>> spells) {
        this.name = name;
        this.spellSoundDirectory = spellSoundDirectory;

        this.spellCategories = copyOf(spellCategories, this.spellSoundDirectory);
        this.spells = copyOf(spells, this.spellSoundDirectory);
    }

    public ChantData(
            String name,
            String spellSoundDirectory,
            ImmutableListMultimap<SpellCategory, TextEntry> spellCategories,
            ImmutableListMultimap<Spell, TextEntry> spells) {
        this.name = name;
        this.spellSoundDirectory = spellSoundDirectory;

        this.spellCategories = spellCategories;
        this.spells = spells;
    }

    /**
     * Safely convert a Map of List into an ImmutableListMultimap.
     */
    private static <K> ImmutableListMultimap<K, TextEntry> copyOf(
            Map<K, List<TextEntry>> in, String soundDirectory) {
        if (in == null || in.isEmpty()) {
            return ImmutableListMultimap.of();
        }

        final Optional<Path> soundPath = Optional.ofNullable(soundDirectory)
                .filter(dir -> !dir.isEmpty())
                .map(Path::of);

        ImmutableListMultimap.Builder<K, TextEntry> builder = ImmutableListMultimap.builder();

        in.forEach((key, values) -> {
            if (key != null && values != null) {
                values.stream()
                        .filter(Objects::nonNull)
                        .map(entry -> withResolvedSound(entry, soundPath))
                        .forEach(entry -> builder.put(key, entry));
            }
        });

        return builder.build();
    }

    private static TextEntry withResolvedSound(TextEntry entry, Optional<Path> soundPath) {
        return soundPath
                .flatMap(dir -> entry.getSound()
                        .filter(s -> !s.isEmpty())
                        .map(s -> entry.withSound(Optional.of(dir.resolve(s).toString()))))
                .orElse(entry);
    }
}