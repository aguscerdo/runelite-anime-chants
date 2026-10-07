package com.animechant;

import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.function.Function;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

public enum Spell {
    UNKNOWN("Unknown", SpellCategory.UNKNOWN),

    // Standard Spellbook - Fire
    FIRE_STRIKE("Fire Strike", SpellCategory.FIRE),
    FIRE_BOLT("Fire Bolt", SpellCategory.FIRE),
    FIRE_BLAST("Fire Blast", SpellCategory.EARTH),
    FIRE_WAVE("Fire Wave", SpellCategory.FIRE),
    FIRE_SURGE("Fire Surge", SpellCategory.FIRE),

    // Standard Spellbook - Air
    WIND_STRIKE("Wind Strike", SpellCategory.AIR),
    WIND_BOLT("Wind Bolt", SpellCategory.AIR),
    WIND_BLAST("Wind Blast", SpellCategory.AIR),
    WIND_WAVE("Wind Wave", SpellCategory.AIR),
    WIND_SURGE("Wind Surge", SpellCategory.AIR),

    // Standard Spellbook - Water
    WATER_STRIKE("Water Strike", SpellCategory.WATER),
    WATER_BOLT("Water Bolt", SpellCategory.WATER),
    WATER_BLAST("Water Blast", SpellCategory.WATER),
    WATER_WAVE("Water Wave", SpellCategory.WATER),
    WATER_SURGE("Water Surge", SpellCategory.WATER),

    // Standard Spellbook - Earth
    EARTH_STRIKE("Earth Strike", SpellCategory.EARTH),
    EARTH_BOLT("Earth Bolt", SpellCategory.EARTH),
    EARTH_BLAST("Earth Blast", SpellCategory.EARTH),
    EARTH_WAVE("Earth Wave", SpellCategory.EARTH),
    EARTH_SURGE("Earth Surge", SpellCategory.EARTH),

    // Standard Spellbook - God Spells
    FLAMES_OF_ZAMORAK("Flames of Zamorak", SpellCategory.UNKNOWN),
    CLAWS_OF_GUTHIX("Claws of Guthix", SpellCategory.UNKNOWN),
    SARADOMIN_STRIKE("Saradomin Strike", SpellCategory.UNKNOWN),

    // Ancient Magicks - Smoke Spells
    SMOKE_RUSH("Smoke Rush", SpellCategory.SMOKE),
    SMOKE_BURST("Smoke Burst", SpellCategory.SMOKE),
    SMOKE_BLITZ("Smoke Blitz", SpellCategory.SMOKE),
    SMOKE_BARRAGE("Smoke Barrage", SpellCategory.SMOKE),

    // Ancient Magicks - Shadow Spells
    SHADOW_RUSH("Shadow Rush", SpellCategory.SHADOW),
    SHADOW_BURST("Shadow Burst", SpellCategory.SHADOW),
    SHADOW_BLITZ("Shadow Blitz", SpellCategory.SHADOW),
    SHADOW_BARRAGE("Shadow Barrage", SpellCategory.SHADOW),

    // Ancient Magicks - Blood Spells
    BLOOD_RUSH("Blood Rush", SpellCategory.BLOOD),
    BLOOD_BURST("Blood Burst", SpellCategory.BLOOD),
    BLOOD_BLITZ("Blood Blitz", SpellCategory.BLOOD),
    BLOOD_BARRAGE("Blood Barrage", SpellCategory.BLOOD),

    // Ancient Magicks - Ice Spells
    ICE_RUSH("Ice Rush", SpellCategory.ICE),
    ICE_BURST("Ice Burst", SpellCategory.ICE),
    ICE_BLITZ("Ice Blitz", SpellCategory.ICE),
    ICE_BARRAGE("Ice Barrage", SpellCategory.ICE),

    // Arceuus - Demonbane
    INFERIOR_DEMONBANE("Inferior Demonbane", SpellCategory.DEMONBANE),
    SUPERIOR_DEMONBANE("Superior Demonbane", SpellCategory.DEMONBANE),
    DARK_DEMONBANE("Dark Demonbane", SpellCategory.DEMONBANE),

    // Arceuus - Thralls
    RESURRECT_LESSER_GHOST("Resurrect Lesser Ghost", SpellCategory.THRALL),
    RESURRECT_LESSER_SKELETON("Resurrect Lesser Skeleton", SpellCategory.THRALL),
    RESURRECT_LESSER_Zombie("Resurrect Lesser Zombie", SpellCategory.THRALL),

    RESURRECT_SUPERIOR_GHOST("Resurrect Superior Ghost", SpellCategory.THRALL),
    RESURRECT_SUPERIOR_SKELETON("Resurrect Superior Skeleton", SpellCategory.THRALL),
    RESURRECT_SUPERIOR_Zombie("Resurrect Superior Zombie", SpellCategory.THRALL),

    RESURRECT_GREATER_GHOST("Resurrect Greater Ghost", SpellCategory.THRALL),
    RESURRECT_GREATER_SKELETON("Resurrect Greater Skeleton", SpellCategory.THRALL),
    RESURRECT_GREATER_Zombie("Resurrect Greater Zombie", SpellCategory.THRALL),

    // Arceuus - Other
    MARK_OF_DARKNESS("Mark of Darkness", SpellCategory.MARK_OF_DARKNESS),
    WARD_OF_ARCEUUS("Ward of Arceuus", SpellCategory.WARD_OF_ARCEUUS),
    DEATH_CHARGE("Death Charge", SpellCategory.DEATH_CHARGE);

    public final String name;
    public final SpellCategory spellCategory;

    Spell(String name, SpellCategory spellCategory) {
        this.name = name;
        this.spellCategory = spellCategory;
    }

    private static final ImmutableMap<String, Spell> BY_NAME = Arrays.stream(values())
            .collect(toImmutableMap(
                    spell -> spell.name,
                    Function.identity()));

    /**
     * Returns a {@link Spell} that matches this name exactly.
     */
    public static Spell fromName(String name) {
        if (name == null) {
            return UNKNOWN;
        }
        return BY_NAME.getOrDefault(name.trim(), UNKNOWN);
    }

    /**
     * Is this {@link Spell} UNKNOWN.
     */
    public boolean isUnknown() {
        return this == Spell.UNKNOWN;
    }
}
