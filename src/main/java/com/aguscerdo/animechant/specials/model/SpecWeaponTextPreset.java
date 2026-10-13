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
    NONE("None (overrides and fallback only)", ""),
    ANIME_MIX("Mix of anime phrases", "");


    private final String label;
    private final String fileName;


    @Override
    public String toString() {
        return label;
    }
}
