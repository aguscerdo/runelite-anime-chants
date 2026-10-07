package com.animechant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * How should delays be handled to show text again when spells are cast.
 */
@Getter
@AllArgsConstructor
public enum SpellCategory {
    UNKNOWN,

    // --- Standard Spellbook --- //
    AIR,
    WATER,
    EARTH,
    FIRE,
    ALCHEMY,

    // --- Ancient Spellbook --- //
    SHADOW,
    SMOKE,
    BLOOD,
    ICE,

    // --- Arceuus --- //
    GRASP,
    DEMONBANE,
    THRALL,
    MARK_OF_DARKNESS,
    WARD_OF_ARCEUUS,
    DEATH_CHARGE,
    REANIMATE;


    /**
     * Returns a SpellCategory from a string or UNKNOWN if it doesn't match
     */
    public static SpellCategory fromString(String s) {
        String upperSnakeName = s.toUpperCase().replace(" ", "_");

        try {
            return SpellCategory.valueOf(upperSnakeName);
        } catch (IllegalArgumentException e) {
            return SpellCategory.UNKNOWN;
        }
    }
}