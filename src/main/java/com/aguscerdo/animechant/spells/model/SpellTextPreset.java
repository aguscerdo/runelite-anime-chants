package com.aguscerdo.animechant.spells.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Bundled chantdata shown in the config dropdown. Contents live in
 * src/main/resources/com/animechant/chantdata/&lt;resourceName&gt;.
 */
@AllArgsConstructor
@Getter
public enum SpellTextPreset {
    NONE("None (overrides only)", ""),
    SPELL_NAME("Spell Name", ""),
    FANTASY("Fantasy", "fantasy.yaml"),
    ANIME_MIX("Anime Mix", "anime_mix.yaml"),
    FRIEREN("Frieren", "frieren.yaml"),
    PALWORLD("Palworld", "palworld.yaml"),
    DEMON_SLAYER("Demon Slayer", "demon_slayer.yaml"),
    SHADOWVERSE("Shadowverse voicelines", "shadowverse.yaml");


    private final String label;
    private final String fileName;


    @Override
    public String toString() {
        return label;
    }
}
