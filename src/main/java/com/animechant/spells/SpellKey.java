package com.animechant.spells;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class SpellKey {
    final Spell spell;
    final SpellCategory spellCategory;

    public SpellKey(Spell spell) {
        this.spell = spell;
        this.spellCategory = SpellCategory.NONE;
    }

    public SpellKey(SpellCategory spellCategory) {
        this.spellCategory = spellCategory;
        this.spell = Spell.UNKNOWN;
    }
}