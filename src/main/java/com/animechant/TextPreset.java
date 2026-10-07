package com.animechant;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;

/**
 * Premade text lists. Keys are lowercase spell / weapon names.
 */
// TODO move this to text files loaded on startup.
// TODO Allow custom spells that override pre-chosen.
public enum TextPreset {
    NONE("NONE", ImmutableMap.of()),
    FANTASY("Fantasy",
            ImmutableMap.<SpellCategory, String>builder()
                    // Standard
                    .put(SpellCategory.FIRE, "Fireball")
                    .put(SpellCategory.AIR, "Gust of Wind")
                    .put(SpellCategory.EARTH, "Stone Cannon")
                    .put(SpellCategory.WATER, "Hydroblast")
                    // Ancient
                    .put(SpellCategory.ICE, "Icicle Lance")
                    .put(SpellCategory.BLOOD, "Vampiric Touch")
                    .put(SpellCategory.SHADOW, "Umbral Strike")
                    .put(SpellCategory.SMOKE, "Ashen Blast")
                    // Aceuus
                    .put(SpellCategory.GRASP, "Arcne Chains")
                    .put(SpellCategory.DEMONBANE, "Cleansing Smite")
                    .put(SpellCategory.THRALL, "Summon Familiar").build()
    ),
    ANIME_MIX("Anime Mix",
            ImmutableMap.<SpellCategory, String>builder()
                    // Standard
                    .put(SpellCategory.FIRE, "Explooooooosiooooon!!")
                    .put(SpellCategory.AIR, "Luminous Wind!")
                    .put(SpellCategory.EARTH, "Stone Cannon")
                    .put(SpellCategory.WATER, "Water Breathing, Fourth Form: Striking Tide!")
                    // Ancient
                    .put(SpellCategory.ICE, "El Minya!")
                    .put(SpellCategory.BLOOD, "Blood Demon Arts: Exploding Blood!")
                    .put(SpellCategory.SHADOW, "I am... Ā ť ø m î ċ")
                    .put(SpellCategory.SMOKE, "Moku Moku no Mi: Howaito Sunēku")
                    // Aceuus
                    .put(SpellCategory.GRASP, "Bakudō #61. Rikujōkōrō")
                    .put(SpellCategory.DEMONBANE, "Zoltraak")
                    .put(SpellCategory.THRALL, "Arise...").build()),
    FRIEREN("Frieren",
            ImmutableMap.<SpellCategory, String>builder()
                    // Standard
                    .put(SpellCategory.FIRE, "Daosdorg")
                    .put(SpellCategory.AIR, "Waldgose")
                    .put(SpellCategory.EARTH, "Doragate")
                    .put(SpellCategory.WATER, "Reamstroha")
                    // Ancient
                    .put(SpellCategory.ICE, "Nephtear")
                    .put(SpellCategory.BLOOD, "Balterie")
                    .put(SpellCategory.SHADOW, "Digardnacht")
                    .put(SpellCategory.SMOKE, "Nebeladora")
                    // Aceuus
                    .put(SpellCategory.GRASP, "Sorganeil")
                    .put(SpellCategory.DEMONBANE, "Zoltraak").build()),
    PALWORLD("Palworld",
            ImmutableMap.<SpellCategory, String>builder()
                    // Standard
                    .put(SpellCategory.FIRE, "Ignis Blast")
                    .put(SpellCategory.AIR, "Crosswind")
                    .put(SpellCategory.EARTH, "Stone Cannon")
                    .put(SpellCategory.WATER, "Hydro jet")
                    // Ancient
                    .put(SpellCategory.ICE, "Iceberg")
                    .put(SpellCategory.BLOOD, "Soul Drain")
                    .put(SpellCategory.SHADOW, "Umbral Surge")
                    .put(SpellCategory.SMOKE, "Smoke Jet")
                    .build()
    );

    @Getter
    private final String label;
    private final ImmutableMap<SpellCategory, String> spells;
    private final String[] fallback;

    TextPreset(String label, ImmutableMap<SpellCategory, String> spells, String... fallback) {
        this.label = label;
        this.spells = spells;
        this.fallback = fallback;
    }

    public String spellText(SpellCategory spellCategory) {
        return spells.getOrDefault(spellCategory, "");
    }


    @Override
    public String toString() {
        return label;
    }
}