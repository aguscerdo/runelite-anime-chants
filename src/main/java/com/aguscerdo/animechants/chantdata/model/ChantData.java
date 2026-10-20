package com.aguscerdo.animechants.chantdata.model;

import com.aguscerdo.animechants.specials.model.SpecWeapon;
import com.aguscerdo.animechants.spells.model.Spell;
import com.aguscerdo.animechants.spells.model.SpellCategory;
import com.google.common.collect.ImmutableListMultimap;
import com.google.gson.*;
import lombok.Value;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

import static com.aguscerdo.animechants.tools.StringUtils.joinPath;

/**
 * Canonical in-memory preset (current schema, version 1). When the schema changes,
 * add a class for the new version and convert older ones into this shape in PresetLoader.
 * <p>
 * Keys are unique in YAML; each key maps to a list of entries, which is why the
 * in-memory form is a multimap (one spell -> many entries, each with its own files).
 */
@Value
public class ChantData {
    public static final int CURRENT_VERSION = 1;

    float version;
    String name;
    String spellSoundDirectory;
    String specWeaponSoundDirectory;

    ImmutableListMultimap<SpellCategory, TextEntry> spellCategories;
    ImmutableListMultimap<Spell, TextEntry> spells;

    ImmutableListMultimap<SpecWeapon, TextEntry> specWeapons;

    public ChantData() {
        version = 0.f;
        name = "";
        spellSoundDirectory = "";
        specWeaponSoundDirectory = "";

        spellCategories = ImmutableListMultimap.of();
        spells = ImmutableListMultimap.of();
        specWeapons = ImmutableListMultimap.of();
    }

    public ChantData(
            Float version,
            String name,
            String spellSoundDirectory,
            String specWeaponSoundDirectory,
            Map<SpellCategory, List<TextEntry>> spellCategories,
            Map<Spell, List<TextEntry>> spells,
            Map<SpecWeapon, List<TextEntry>> specWeapons
    ) {
        this.version = version;
        this.name = name;
        this.spellSoundDirectory = spellSoundDirectory;
        this.specWeaponSoundDirectory = specWeaponSoundDirectory;

        this.spellCategories = copyOf(spellCategories, this.spellSoundDirectory);
        this.spells = copyOf(spells, this.spellSoundDirectory);
        this.specWeapons = copyOf(specWeapons, this.specWeaponSoundDirectory);
    }

    public ChantData(
            float version,
            String name,
            String spellSoundDirectory,
            String specWeaponSoundDirectory,
            ImmutableListMultimap<SpellCategory, TextEntry> spellCategories,
            ImmutableListMultimap<Spell, TextEntry> spells,
            ImmutableListMultimap<SpecWeapon, TextEntry> specWeapons

    ) {
        this.version = version;
        this.name = name;
        this.spellSoundDirectory = spellSoundDirectory;
        this.specWeaponSoundDirectory = specWeaponSoundDirectory;

        this.spellCategories = spellCategories;
        this.spells = spells;
        this.specWeapons = specWeapons;
    }

    /**
     * Safely convert a Map of List into an ImmutableListMultimap.
     */
    private static <K> ImmutableListMultimap<K, TextEntry> copyOf(
            Map<K, List<TextEntry>> in, String soundDirectory) {
        if (in == null || in.isEmpty()) {
            return ImmutableListMultimap.of();
        }

        final Optional<String> soundDir = Optional.ofNullable(soundDirectory)
                .filter(dir -> !dir.isEmpty());

        ImmutableListMultimap.Builder<K, TextEntry> builder = ImmutableListMultimap.builder();

        in.forEach((key, values) -> {
            if (key != null && values != null) {
                values.stream()
                        .filter(Objects::nonNull)
                        .map(entry -> withResolvedSound(entry, soundDir))
                        .forEach(entry -> builder.put(key, entry));
            }
        });

        return builder.build();
    }


    private static TextEntry withResolvedSound(TextEntry entry, Optional<String> soundDir) {
        return soundDir
                .flatMap(dir -> entry.getSound()
                        .filter(s -> !s.isEmpty())
                        .map(s -> entry.withSound(Optional.of(joinPath(dir, s)))))
                .orElse(entry);
    }

    public static class Deserializer implements JsonDeserializer<ChantData> {
        private static String string(JsonObject obj, String field) {
            JsonElement el = obj.get(field);
            return el == null || el.isJsonNull() ? "" : el.getAsString();
        }

        /**
         * Keys are read as strings and resolved by the enum's fromString (unknown keys merge
         * instead of colliding). A single entry is accepted in place of a list.
         */
        private static <K> Map<K, List<TextEntry>> entries(
                JsonObject root, String field, JsonDeserializationContext ctx,
                Function<String, K> keyParser) {
            Map<K, List<TextEntry>> out = new LinkedHashMap<>();
            JsonElement section = root.get(field);
            if (section == null || section.isJsonNull()) {
                return out;
            }

            for (Map.Entry<String, JsonElement> e : section.getAsJsonObject().entrySet()) {
                List<TextEntry> list = out.computeIfAbsent(keyParser.apply(e.getKey()), k -> new ArrayList<>());
                JsonElement value = e.getValue();
                if (value.isJsonArray()) {
                    for (JsonElement item : value.getAsJsonArray()) {
                        list.add(ctx.deserialize(item, TextEntry.class));
                    }
                } else {
                    list.add(ctx.deserialize(value, TextEntry.class));
                }
            }

            return out;
        }

        @Override
        public ChantData deserialize(JsonElement json, Type type, JsonDeserializationContext ctx)
                throws JsonParseException {
            JsonObject root = json.getAsJsonObject();

            float version = root.has("version") ? root.get("version").getAsFloat() : CURRENT_VERSION;
            String name = string(root, "name");
            String spellDir = string(root, "spell_sound_directory");
            String specDir = string(root, "spec_weapon_sound_directory");

            return new ChantData(
                    version, name, spellDir, specDir,
                    copyOf(entries(root, "spell_categories", ctx, SpellCategory::fromString), spellDir),
                    copyOf(entries(root, "spells", ctx, Spell::fromString), spellDir),
                    copyOf(entries(root, "spec_weapons", ctx, SpecWeapon::fromString), specDir));
        }
    }
}