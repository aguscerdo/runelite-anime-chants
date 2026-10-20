package com.aguscerdo.animechants.specials.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Bundled chantdata shown in the config dropdown. Contents live in
 * src/main/resources/com/animechants/chantdata/resourceName;.
 */
@AllArgsConstructor
@Getter
public enum SpecWeaponTextPreset {
    NONE("None", "", ""),
    ANIME_MIX("Anime Mix", "anime_mix", "anime_mix.json"),
    SHADOWVERSE("Shadowverse voicelines", "shadowverse", "shadowverse.json"),
    USER_CUSTOM("User Custom", "user_custom", "user_custom.json");


    private final String label;
    private final String directoryName;
    private final String fileName;


    @Override
    public String toString() {
        return label;
    }
}
