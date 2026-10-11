package com.aguscerdo.animechant.spells.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AutoCastSpellTest {
    @Test
    public void spellFromAutoCast_success() {
        assertEquals(AutoCastSpell.spellFromAutoCast(45), Spell.BLOOD_BARRAGE);
    }

    @Test
    public void spellFromAutoCast_unknwonVarbit_returnsUnknown() {
        assertEquals(AutoCastSpell.spellFromAutoCast(500), Spell.UNKNOWN);
    }
}
