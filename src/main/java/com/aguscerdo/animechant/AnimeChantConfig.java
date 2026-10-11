package com.aguscerdo.animechant;

import com.aguscerdo.animechant.spells.model.SpellTextPreset;
import net.runelite.client.config.*;

@ConfigGroup(AnimeChantConfig.GROUP)
public interface AnimeChantConfig extends Config {
    String GROUP = "anime-chant";

    @ConfigSection(
            name = "Spells",
            description = "Settings for text shown on spell cast",
            position = 1,
            closedByDefault = false
    )
    String SPELL_SECTION = "anime-chant-spells";


    @ConfigItem(
            keyName = "spellsEnabled",
            name = "Enable Spell Chanting",
            description = "Display text when you cast a spell",
            section = SPELL_SECTION,
            position = 0
    )
    default boolean spellsEnabled() {
        return true;
    }

    String SPELL_PRESET = "spellPrest";

    @ConfigItem(
            keyName = SPELL_PRESET,
            name = "Spell Text Preset",
            description = "Bundled list of spell text. Your overrides always take priority.",
            section = SPELL_SECTION,
            position = 1
    )
    default SpellTextPreset spellPreset() {
        return SpellTextPreset.FANTASY;
    }

    String SPELL_OVERRIDES = "customSpellOverrides";

    @ConfigItem(
            keyName = SPELL_OVERRIDES,
            name = "Custom Spell Overrides",
            description = "Custom user spell overrides.<br>" +
                    "Each line must contain one entry formatted as follows:<br><br>" +
                    "--------------------------------------------------------<br>" +
                    "Spell Name=Text to Display<br>" +
                    "Spell Category name=Text to Display<br>" +
                    "Spell Name File=Path to <b>.wav</b> sound file from runescape folder<br>" +
                    "--------------------------------------------------------<br><br>" +
                    "<b>Usage Notes:</b><br>" +
                    "- Multiple entries with the same name will all be used<br>" +
                    "- Multiple file paths for the same spell will be applied in order<br>" +
                    "- User entries take priority over spell presets or spell names<br>" +
                    "- Setting a spell to <b>None</b> or <b>empty</b> will disable it<br>" +
                    "- Invalid lines will be ignored",
            section = SPELL_SECTION,
            position = 2
    )
    default String customSpellOverrides() {
        return "";
    }


    @Range(min = 0)
    @ConfigItem(
            keyName = "spellTextDelayTicks",
            name = "Spell text delay (ticks)",
            description = "Text is hidden if the same spell/category showed text less than N ticks ago.",
            section = SPELL_SECTION,
            position = 3
    )
    default int spellTextDelayTicks() {
        return 0;
    }

    @Range(min = 0)
    @ConfigItem(
            keyName = "spellTextDelayFromLastCastTicks",
            name = "Spell text delay from last cast (ticks)",
            description = "Text is hidden if the same spell/category was cast less than N ticks ago "
                    + "(even if that cast showed nothing).<br>" +
                    "Spell-name text and category text are tracked separately.",
            section = SPELL_SECTION,
            position = 4
    )
    default int spellTextDelayFromLastCastTicks() {
        return 0;
    }

    @ConfigItem(
            keyName = "shoutSpells",
            name = "Shout Spells",
            description = "Make spells more intense by adding ! at the end of every chant!<br>" +
                    "No change if text ends with dot `.` or exclamation point `!`.",
            section = SPELL_SECTION,
            position = 5
    )
    default boolean shoutSpells() {
        return false;
    }

    @ConfigItem(
            keyName = "fallbackToSpellName",
            name = "Fall back to Spell Name",
            description = "When enabled fall back to displaying the spells name if there are no matches.",
            section = SPELL_SECTION,
            position = 6
    )
    default boolean fallbackToSpellName() {
        return false;
    }

    @ConfigItem(
            keyName = "spellSoundsEnabled",
            name = "Spell Sounds",
            description = "Play sounds (if any) when chanting a spell.",
            section = SPELL_SECTION,
            position = 7
    )
    default boolean spellSoundsEnabled() {
        return false;
    }

    @Range(min = 0, max = 100)
    @ConfigItem(
            keyName = "soundVolume",
            name = "Sound Volume",
            description = "Volume of chant sounds",
            position = 5
    )
    default int soundVolume() {
        return 65;
    }


    @Range(min = 3, max = 15)
    @ConfigItem(
            keyName = "durationTicks",
            name = "Display duration (ticks)",
            description = "How long the text stays above your head",
            position = 6
    )
    default int durationTicks() {
        return 3;
    }


    @ConfigItem(
            keyName = "scratchPad",
            name = "Scratchpad (no effect)",
            description = "A scratchpad to copy and paste differnet presets you may have.",
            position = 7
    )
    default String scratchPad() {
        return "";
    }
}
