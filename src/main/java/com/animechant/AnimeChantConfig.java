package com.animechant;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup(AnimeChantConfig.GROUP)
public interface AnimeChantConfig extends Config {
    String GROUP = "anime-chant";

    @ConfigSection(
            name = "Spell text",
            description = "Settings for text shown on spell cast",
            position = 1,
            closedByDefault = false
    )
    String SPELL_SECTION = "spellSection";


    @ConfigItem(
            keyName = "spellsEnabled",
            name = "Show text on spell cast",
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
            name = "Spell text list",
            description = "Bundled list of spell text. Your overrides always take priority.",
            section = SPELL_SECTION,
            position = 1
    )
    default TextPreset spellPreset() {
        return TextPreset.FANTASY;
    }

//    @ConfigItem(
//            keyName = "customSpellText",
//            name = "Overrides",
//            description = "Format: Spell name=Text; category:Fire=Text. Spell names beat categories. "
//                    + "Repeat a key for random variants. You can also put a YAML file at "
//                    + "~/.runelite/anime-chant/overrides.yaml (same format as the bundled presets).",
//            section = SPELL_SECTION,
//            position = 2
//    )
//    default String spellOverrides() {
//        return "";
//    }

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
                    + "(even if that cast showed nothing). Spell-name text and category text are tracked separately.",
            section = SPELL_SECTION,
            position = 4
    )
    default int spellTextDelayFromLastCastTicks() {
        return 0;
    }

    @ConfigItem(
            keyName = "shoutSpells",
            name = "Shout Spells",
            description = "Make spells more intense by adding ! at the end of every chant!",
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

    @Range(min = 3, max = 12)
    @ConfigItem(
            keyName = "durationTicks",
            name = "Display duration (ticks)",
            description = "How long the text stays above your head",
            section = SPELL_SECTION,
            position = 5
    )
    default int durationTicks() {
        return 3;
    }
}
