package com.animechant;

import lombok.Getter;

/**
 * Bundled presets shown in the config dropdown. Contents live in
 * src/main/resources/com/animechant/presets/&lt;resourceName&gt;.
 */
@Getter
public enum TextPreset {
    NONE("None (overrides only)", null),
    FANTASY("Fantasy", "fantasy.yaml"),
    ANIME_MIX("Anime Mix", "anime_mix.yaml"),
    FRIEREN("Frieren", "frieren.yaml"),
    PALWORLD("Palworld", "palworld.yaml");

    private final String label;
    /** Null when the preset has no file. */
    private final String resourceName;

    TextPreset(String label, String resourceName) {
        this.label = label;
        this.resourceName = resourceName;
    }

    @Override
    public String toString() {
        return label;
    }
}
