package com.aguscerdo.animechant.spells.model;


import com.aguscerdo.animechant.chantdata.TextEntry;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class SpellTest {

    @Test
    public void fromString_null_unknownSpell() {
        assertEquals(
                Spell.fromString(null),
                Spell.UNKNOWN
        );
    }

    @Test
    public void fromString_empty_unknownSpell() {
        assertEquals(
                Spell.fromString(""),
                Spell.UNKNOWN
        );
    }

    @Test
    public void fromString_withUpperCase() {
        assertEquals(
                Spell.fromString("High Level Alchemy"),
                Spell.HIGH_LEVEL_ALCHEMY
        );
    }


    @Test
    public void fromString_lowerCase() {
        assertEquals(
                Spell.fromString("high level alchemy"),
                Spell.HIGH_LEVEL_ALCHEMY
        );
    }


    @Test
    public void fromString_untrimmed() {
        assertEquals(
                Spell.fromString("  high level alchemy  "),
                Spell.HIGH_LEVEL_ALCHEMY
        );
    }


    @Test
    public void fromString_failedParsing_unknownSpell() {
        assertEquals(
                Spell.fromString("Ashes to Mashes"),
                Spell.UNKNOWN
        );
    }


    @Test
    public void fromString_altNames_resolveToSpell() {
        assertEquals(
                Spell.fromString("enchant sapphire"),
                Spell.LVL_1_ENCHANT
        );
    }

    @Test
    public void asTextEntry_returnsTextEntry() {
        assertEquals(
                Spell.HIGH_LEVEL_ALCHEMY.asTextEntry(),
                new TextEntry("High Level Alchemy", Optional.empty())
        );
    }

    @Test
    public void animationIdMatches_yes() {
        assertTrue(Spell.HIGH_LEVEL_ALCHEMY.animationIdMatches(713));
        assertTrue(Spell.HIGH_LEVEL_ALCHEMY.animationIdMatches(10624));
    }

    @Test
    public void animationIdMatches_no() {
        assertFalse(Spell.HIGH_LEVEL_ALCHEMY.animationIdMatches(12345));
    }

    @Test
    public void timeoutTicks_worldTargetted_shortTimeout() {
        assertEquals(Spell.FIRE_BLAST.timeoutTicks(), Spell.WORLD_TARGETED_TIMEOUT_TICKS);
    }


    @Test
    public void timeoutTicks_nonWorldTargetted_shortTimeout() {
        assertEquals(Spell.HIGH_LEVEL_ALCHEMY.timeoutTicks(), Spell.INSTANT_TIMEOUT_TICKS);
        assertEquals(Spell.TELEPORT_TO_HOUSE.timeoutTicks(), Spell.INSTANT_TIMEOUT_TICKS);
    }


    @Test
    public void test_isUnknown() {
        assertTrue(Spell.UNKNOWN.isUnknown());

        assertFalse(Spell.DEMONIC_OFFERING.isUnknown());
    }

}
