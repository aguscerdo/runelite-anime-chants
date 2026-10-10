package com.aguscerdo.animechant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Bundled presets shown in the config dropdown. Contents live in
 * src/main/resources/com/animechant/presets/&lt;resourceName&gt;.
 */
@AllArgsConstructor
@Getter
public enum TextPreset {
    NONE("None (overrides only)", ""),
    SPELL_NAME("Spell Name", ""),
    FANTASY("Fantasy", "fantasy.yaml"),
    ANIME_MIX("Anime Mix", "anime_mix.yaml"),
    FRIEREN("Frieren", "frieren.yaml"),
    PALWORLD("Palworld", "palworld.yaml"),
    DEMON_SLAYER("Demon Slayer", "demon_slayer.yaml");

    private final String label;
    private final String fileName;


    @Override
    public String toString() {
        return label;
    }
}
