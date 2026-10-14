package com.aguscerdo.animechant.spells.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Bundled chantdata shown in the config dropdown. Contents live in
 * src/main/resources/com/animechant/chantdata/resourceName;.
 */
@AllArgsConstructor
@Getter
public enum SpellTextPreset {
    NONE("None", "", ""),
    FANTASY("Fantasy", "fantasy", "fantasy.yaml"),
    ANIME_MIX("Anime Mix", "anime_mix", "anime_mix.yaml"),
    FRIEREN("Frieren", "frieren", "frieren.yaml"),
    PALWORLD("Palworld", "palworld", "palworld.yaml"),
    DEMON_SLAYER("Demon Slayer", "demon_slayer", "demon_slayer.yaml"),
    SHADOWVERSE("Shadowverse", "shadowverse", "shadowverse.yaml"),
    USER_CUSTOM("User Custom", "user_custom", "user_custom.yaml");


    private final String label;
    private final String directoryName;
    private final String fileName;


    @Override
    public String toString() {
        return label;
    }
}
