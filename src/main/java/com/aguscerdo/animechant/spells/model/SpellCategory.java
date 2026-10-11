package com.aguscerdo.animechant.spells.model;

/**
 * Groups of related spells. Used for text lookup and for delay tracking when a
 * preset gives text for a whole category rather than a single spell.
 */
public enum SpellCategory {
    UNKNOWN,
    // --- General --- //
    TELEPORT,
    TELE_OTHER,
    TELE_GROUP,

    // --- Standard spellbook --- //
    AIR,
    WATER,
    EARTH,
    FIRE,
    GOD,
    BIND,
    ALCHEMY,
    CURSE,
    ENCHANT,
    CHARGE_ORB,

    // --- Ancient spellbook --- //
    SHADOW,
    SMOKE,
    BLOOD,
    ICE,

    // --- Arceuus --- //
    GRASP,
    DEMONBANE,
    THRALL,
    REANIMATE,
    OFFERING,
    CORRUPTION,

    // --- Lunar --- //
    CURE,
    HEAL,
    VENGEANCE;

    /**
     * Returns a SpellCategory from a string ("Mark of Darkness", "fire", ...) or UNKNOWN.
     */
    public static SpellCategory fromString(String s) {
        if (s == null) {
            return UNKNOWN;
        }

        try {
            return valueOf(s.trim().toUpperCase().replace(' ', '_'));
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    public boolean isUnknown() {
        return this == SpellCategory.UNKNOWN;
    }
}
