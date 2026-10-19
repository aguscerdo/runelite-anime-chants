package com.aguscerdo.animechant.specials.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Bundled chantdata shown in the config dropdown. Contents live in
 * src/main/resources/com/animechant/chantdata/resourceName;.
 */
@AllArgsConstructor
@Getter
public enum SpecWeaponTextPreset {
    NONE("None", "", ""),
    ANIME_MIX("Anime Mix", "anime_mix", "anime_mix.yaml"),
    SHADOWVERSE("Shadowverse voicelines", "shadowverse", "shadowverse.yaml"),
    USER_CUSTOM("User Custom", "user_custom", "user_custom.yaml");


    private final String label;
    private final String directoryName;
    private final String fileName;


    @Override
    public String toString() {
        return label;
    }
}
