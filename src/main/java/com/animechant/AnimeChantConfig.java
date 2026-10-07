package com.animechant;


import net.runelite.client.config.*;

@ConfigGroup(AnimeChantConfig.GROUP)
public interface AnimeChantConfig extends Config {
    String GROUP = "anime-chant";

    @ConfigSection(
            name = "Spell text",
            description = "Settings for text shown on spell cast",
            position = 1,
            closedByDefault = false
    )
    String SPELL_SECTION = "Spell Section Settings";

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

    @Range(min = 0)
    @ConfigItem(keyName = "spellTextDelayTicks",
            name = "Spell text delay (ticks)",
            description = "Text will be shown after N ticks from last display.",
            section = SPELL_SECTION,
            position = 1)
    default int spellTextDelayTicks() {
        return 0;
    }

    @Range(min = 0)
    @ConfigItem(keyName = "spellTextDelayFromLastCastTicks",
            name = "Spell text delay from last cast (ticks)",
            description = "Text will only be shown after N ticks from the last time a spell has been cast. Different spells are tracked separately.",
            section = SPELL_SECTION,
            position = 2)
    default int spellTextDelayFromLastCastTicks() {
        return 0;
    }


    @ConfigItem(keyName = "spellPreset", name = "Spell text list", description = "Premade list, or Custom to use your own below", position = 1)
    default TextPreset spellPreset() {
        return TextPreset.FANTASY;
    }


    @ConfigItem(keyName = "customSpellText",
            name = "Custom spell text",
            description = "Format: Spell name=Text; Spell name=Text. Used when the list is Custom",
            hidden = true,
            section = SPELL_SECTION,
            position = 4)
    default String customSpellText() {
        return "Fire Strike=Burn!; Wind Strike=Whoosh!";
    }


    @Range(min = 3, max = 12)
    @ConfigItem(keyName = "durationTicks", name = "Display duration (ticks)", description = "How long the text stays above your head", position = 10)
    default int durationTicks() {
        return 3;
    }
}