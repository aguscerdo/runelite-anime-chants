package com.animechant.enums;

import com.animechant.enums.Spell;
import com.animechant.enums.SpellCategory;

public final class SpellOrCategory {
    final Spell spell;
    final SpellCategory spellCategory;

    public SpellOrCategory(Spell spell) {
        this.spell = spell;
        this.spellCategory = SpellCategory.NONE;
    }

    public SpellOrCategory(SpellCategory spellCategory) {
        this.spellCategory = spellCategory;
        this.spell = Spell.UNKNOWN;
    }
}