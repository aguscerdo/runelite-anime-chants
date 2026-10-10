package com.aguscerdo.animechant.spells;

public enum SpellCastType {
    UNKNOWN,
    // Spell is usually cast instantly.
    // E.g. Teleports, Vengeance
    INSTANT,
    // Spell is cast onto an item in the inventory.
    // E.g. Enchant, High Level Alchemy
    INVENTORY_TARGET,
    // Spell is cast onto an entity not in the inventory.
    // e.g. Combat spells, Orb
    WORLD_TARGET,
}
