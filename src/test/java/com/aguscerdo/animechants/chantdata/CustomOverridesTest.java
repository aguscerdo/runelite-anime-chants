package com.aguscerdo.animechants.chantdata;

import com.aguscerdo.animechants.chantdata.model.TextEntry;
import com.aguscerdo.animechants.chantdata.model.ValidationIssue;
import com.aguscerdo.animechants.specials.model.SpecWeapon;
import com.aguscerdo.animechants.spells.model.Spell;
import com.aguscerdo.animechants.spells.model.SpellCategory;
import com.google.common.collect.ImmutableListMultimap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CustomOverridesTest {

    @Test
    public void parseOverrides_nullText_empty() {
        assertEquals(
                CustomOverrides.parseSpellOverrides(null),
                new CustomOverrides.OverridesResult()
        );
    }

    @Test
    public void parseOverrides_blankText_empty() {
        assertEquals(
                CustomOverrides.parseSpellOverrides("  "),
                new CustomOverrides.OverridesResult()
        );
    }


    @Test
    public void parseOverrides_noEquals_invalid() {
        assertThat(CustomOverrides.parseSpellOverrides("key").getValidationIssues()).containsExactly(
                new ValidationIssue("key", ValidationIssue.ValidationIssueReason.LINE_NOT_SEPARATED_BY_EQUALS)
        );
    }

    @Test
    public void parseOverrides_fileNotEndsInWav_invalid() {
        assertThat(CustomOverrides.parseSpellOverrides("my file=sounds.mp3").getValidationIssues()).containsExactly(
                new ValidationIssue("my file=sounds.mp3", ValidationIssue.ValidationIssueReason.FILES_MUST_END_IN_WAV)
        );
    }

    @Test
    public void parseOverrides_contentTooLong_invalid() {
        assertThat(CustomOverrides.parseSpellOverrides("key=1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890abc").getValidationIssues()).containsExactly(
                new ValidationIssue("key=123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123...", ValidationIssue.ValidationIssueReason.CONTENT_TOO_LONG));
    }

    @Test
    public void parseOverrides_noKeyMatches_invalid() {
        assertThat(CustomOverrides.parseSpellOverrides("nope=nope").getValidationIssues()).containsExactly(
                new ValidationIssue("nope=nope", ValidationIssue.ValidationIssueReason.KEY_DOESNT_MATCH_ANYTHING));
    }

    @Test
    public void parseSpellOverrides_matchesSpell() {
        assertEquals(
                CustomOverrides.parseSpellOverrides("Varrock Teleport=Whoosh").getChantData().getSpells(),
                ImmutableListMultimap.of(Spell.VARROCK_TELEPORT, new TextEntry("Whoosh", Optional.empty()))
        );
    }

    @Test
    public void parseOverrides_skipEmptyLines_valid() {
        assertEquals(
                CustomOverrides.parseSpellOverrides("Varrock Teleport=Whoosh\n  \n\n\n  \n").getChantData().getSpells(),
                ImmutableListMultimap.of(Spell.VARROCK_TELEPORT, new TextEntry("Whoosh", Optional.empty()))
        );

    }


    @Test
    public void parseSpellOverrides_matchesSpellEmptyContent_contentSetToNone() {
        assertEquals(
                CustomOverrides.parseSpellOverrides("Varrock Teleport=").getChantData().getSpells(),
                ImmutableListMultimap.of(Spell.VARROCK_TELEPORT, new TextEntry("NONE", Optional.empty()))
        );
    }

    @Test
    public void parseSpellOverrides_contentIsNoneCaseInsensitive_contentSetToNone() {
        assertEquals(
                CustomOverrides.parseSpellOverrides("Varrock Teleport=none").getChantData().getSpells(),
                ImmutableListMultimap.of(Spell.VARROCK_TELEPORT, new TextEntry("NONE", Optional.empty()))
        );
    }

    @Test
    public void parseSpellOverrides_matchesSpellCaseInsensitive() {
        assertEquals(
                CustomOverrides.parseSpellOverrides("VaRRoCK TeLePoRT=Whoosh").getChantData().getSpells(),
                ImmutableListMultimap.of(Spell.VARROCK_TELEPORT, new TextEntry("Whoosh", Optional.empty()))
        );
    }

    @Test
    public void parseSpellOverrides_matchesSpellFile() {
        assertEquals(
                CustomOverrides.parseSpellOverrides("Varrock Teleport file=Whoosh.wav")
                        .getChantData().getSpells(),
                ImmutableListMultimap.of(Spell.VARROCK_TELEPORT, new TextEntry("", Optional.of("Whoosh.wav")))
        );
    }

    @Test
    public void parseSpellOverrides_spellAndFileMergeTogether() {
        assertEquals(
                CustomOverrides.parseSpellOverrides("Varrock Teleport=Whoosh\nVarrock Teleport file=Whoosh.wav")
                        .getChantData().getSpells(),
                ImmutableListMultimap.of(Spell.VARROCK_TELEPORT, new TextEntry("Whoosh", Optional.of("Whoosh.wav")))
        );
    }


    @Test
    public void parseSpellOverrides_matchesSpellCategory() {
        assertEquals(
                CustomOverrides.parseSpellOverrides("Teleport=Whoosh").getChantData().getSpellCategories(),
                ImmutableListMultimap.of(SpellCategory.TELEPORT, new TextEntry("Whoosh", Optional.empty()))
        );
    }

    @Test
    public void parseSpellOverrides_matchesSpellCategoryFile() {
        assertEquals(
                CustomOverrides.parseSpellOverrides("Teleport file=Whoosh.wav")
                        .getChantData().getSpellCategories(),
                ImmutableListMultimap.of(SpellCategory.TELEPORT, new TextEntry("", Optional.of("Whoosh.wav")))
        );
    }

    @Test
    public void parseSpellOverrides_spellCategoryAndFileMergeTogether() {
        assertEquals(
                CustomOverrides.parseSpellOverrides("Teleport=Whoosh\nTeleport file=Whoosh.wav")
                        .getChantData().getSpellCategories(),
                ImmutableListMultimap.of(SpellCategory.TELEPORT, new TextEntry("Whoosh", Optional.of("Whoosh.wav")))
        );
    }


    @Test
    public void parseSpecWeaponOverrides_matchesName() {
        assertEquals(
                CustomOverrides.parseSpecWeaponOverrides("Abyssal Whip=Whoosh").getChantData().getSpecWeapons(),
                ImmutableListMultimap.of(SpecWeapon.ABYSSAL_WHIP, new TextEntry("Whoosh", Optional.empty()))
        );
    }

    @Test
    public void parseSpecWeaponOverrides_matchesSpellEmptyContent_contentSetToNone() {
        assertEquals(
                CustomOverrides.parseSpecWeaponOverrides("Abyssal Whip=").getChantData().getSpecWeapons(),
                ImmutableListMultimap.of(SpecWeapon.ABYSSAL_WHIP, new TextEntry("NONE", Optional.empty()))
        );
    }

    @Test
    public void parseSpecWeaponOverrides_contentIsNoneCaseInsensitive_contentSetToNone() {
        assertEquals(
                CustomOverrides.parseSpecWeaponOverrides("Abyssal Whip=none").getChantData().getSpecWeapons(),
                ImmutableListMultimap.of(SpecWeapon.ABYSSAL_WHIP, new TextEntry("NONE", Optional.empty()))
        );
    }

    @Test
    public void parseSpecWeaponOverrides_matchesSpecWeaponCaseInsensitive() {
        assertEquals(
                CustomOverrides.parseSpecWeaponOverrides("aBYsSaL WHiP=Whoosh").getChantData().getSpecWeapons(),
                ImmutableListMultimap.of(SpecWeapon.ABYSSAL_WHIP, new TextEntry("Whoosh", Optional.empty()))
        );
    }

    @Test
    public void parseSpecWeaponOverrides_matchesSpecWeaponFile() {
        assertEquals(
                CustomOverrides.parseSpecWeaponOverrides("Abyssal Whip file=Whoosh.wav")
                        .getChantData().getSpecWeapons(),
                ImmutableListMultimap.of(SpecWeapon.ABYSSAL_WHIP, new TextEntry("", Optional.of("Whoosh.wav")))
        );
    }

    @Test
    public void parseSpecWeaponOverrides_weaponAndFileMerge() {
        assertEquals(
                CustomOverrides.parseSpecWeaponOverrides("Abyssal Whip=Whoosh\nAbyssal Whip file=Whoosh.wav")
                        .getChantData().getSpecWeapons(),
                ImmutableListMultimap.of(SpecWeapon.ABYSSAL_WHIP, new TextEntry("Whoosh", Optional.of("Whoosh.wav")))
        );
    }


    @Test
    public void parseSpecWeaponOverrides_matchesById() {
        assertEquals(
                CustomOverrides.parseSpecWeaponOverrides("4151=Whoosh").getChantData().getSpecWeapons(),
                ImmutableListMultimap.of(SpecWeapon.ABYSSAL_WHIP, new TextEntry("Whoosh", Optional.empty()))
        );
    }

    @Test
    public void parseSpecWeaponOverrides_matchesbyIdFile() {
        assertEquals(
                CustomOverrides.parseSpecWeaponOverrides("4151 file=Whoosh.wav")
                        .getChantData().getSpecWeapons(),
                ImmutableListMultimap.of(SpecWeapon.ABYSSAL_WHIP, new TextEntry("", Optional.of("Whoosh.wav")))
        );
    }

    @Test
    public void parseSpecWeaponOverrides_byIdMergeTextAndFile() {
        assertEquals(
                CustomOverrides.parseSpecWeaponOverrides("4151=Whoosh\n4151 file=Whoosh.wav")
                        .getChantData().getSpecWeapons(),
                ImmutableListMultimap.of(SpecWeapon.ABYSSAL_WHIP, new TextEntry("Whoosh", Optional.of("Whoosh.wav")))
        );
    }

}
