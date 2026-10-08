package com.animechant.enums;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Function;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

/**
 * Known spells and the category they belong to. A spell that isn't listed here can
 * still get text through a spell-name entry in a preset/override.
 */
@AllArgsConstructor
@Getter
public enum Spell {
    UNKNOWN("Unknown", SpellCategory.NONE),

    // Standard - Fire
    FIRE_STRIKE("Fire Strike", SpellCategory.FIRE),
    FIRE_BOLT("Fire Bolt", SpellCategory.FIRE),
    FIRE_BLAST("Fire Blast", SpellCategory.FIRE),
    FIRE_WAVE("Fire Wave", SpellCategory.FIRE),
    FIRE_SURGE("Fire Surge", SpellCategory.FIRE),

    // Standard - Air
    WIND_STRIKE("Wind Strike", SpellCategory.AIR),
    WIND_BOLT("Wind Bolt", SpellCategory.AIR),
    WIND_BLAST("Wind Blast", SpellCategory.AIR),
    WIND_WAVE("Wind Wave", SpellCategory.AIR),
    WIND_SURGE("Wind Surge", SpellCategory.AIR),

    // Standard - Water
    WATER_STRIKE("Water Strike", SpellCategory.WATER),
    WATER_BOLT("Water Bolt", SpellCategory.WATER),
    WATER_BLAST("Water Blast", SpellCategory.WATER),
    WATER_WAVE("Water Wave", SpellCategory.WATER),
    WATER_SURGE("Water Surge", SpellCategory.WATER),

    // Standard - Earth
    EARTH_STRIKE("Earth Strike", SpellCategory.EARTH),
    EARTH_BOLT("Earth Bolt", SpellCategory.EARTH),
    EARTH_BLAST("Earth Blast", SpellCategory.EARTH),
    EARTH_WAVE("Earth Wave", SpellCategory.EARTH),
    EARTH_SURGE("Earth Surge", SpellCategory.EARTH),

    // Standard - God spells
    FLAMES_OF_ZAMORAK("Flames Of Zamorak", SpellCategory.GOD),
    CLAWS_OF_GUTHIX("Claws Of Guthix", SpellCategory.GOD),
    SARADOMIN_STRIKE("Saradomin Strike", SpellCategory.GOD),

    // Standard - Bind
    BIND("Bind", SpellCategory.BIND),
    SNARE("Snare", SpellCategory.BIND),
    ENTANGLE("Entangle", SpellCategory.BIND),

    // Standard - Curses / debuffs
    CONFUSE("Confuse", SpellCategory.CURSE),
    WEAKEN("Weaken", SpellCategory.CURSE),
    CURSE("Curse", SpellCategory.CURSE),
    VULNERABILITY("Vulnerability", SpellCategory.CURSE),
    ENFEEBLE("Enfeeble", SpellCategory.CURSE),
    STUN("Stun", SpellCategory.CURSE),
    TELE_BLOCK("Tele Block", SpellCategory.CURSE),

    // Standard - Alchemy
    LOW_LEVEL_ALCHEMY("Low Level Alchemy", SpellCategory.ALCHEMY),
    HIGH_LEVEL_ALCHEMY("High Level Alchemy", SpellCategory.ALCHEMY),

    // Standard - Enchant (jewellery)
    ENCHANT_SAPPHIRE_JEWELLERY("Enchant Sapphire Jewellery", SpellCategory.ENCHANT),
    ENCHANT_EMERALD_JEWELLERY("Enchant Emerald Jewellery", SpellCategory.ENCHANT),
    ENCHANT_RUBY_JEWELLERY("Enchant Ruby Jewellery", SpellCategory.ENCHANT),
    ENCHANT_DIAMOND_JEWELLERY("Enchant Diamond Jewellery", SpellCategory.ENCHANT),
    ENCHANT_DRAGONSTONE_JEWELLERY("Enchant Dragonstone Jewellery", SpellCategory.ENCHANT),
    ENCHANT_ONYX_JEWELLERY("Enchant Onyx Jewellery", SpellCategory.ENCHANT),
    ENCHANT_ZENYTE_JEWELLERY("Enchant Zenyte Jewellery", SpellCategory.ENCHANT),

    // Standard - Enchant (crossbow bolts)
    ENCHANT_OPAL_BOLT("Enchant Opal Bolt", SpellCategory.ENCHANT),
    ENCHANT_SAPPHIRE_BOLT("Enchant Sapphire Bolt", SpellCategory.ENCHANT),
    ENCHANT_JADE_BOLT("Enchant Jade Bolt", SpellCategory.ENCHANT),
    ENCHANT_PEARL_BOLT("Enchant Pearl Bolt", SpellCategory.ENCHANT),
    ENCHANT_EMERALD_BOLT("Enchant Emerald Bolt", SpellCategory.ENCHANT),
    ENCHANT_TOPAZ_BOLT("Enchant Topaz Bolt", SpellCategory.ENCHANT),
    ENCHANT_RUBY_BOLT("Enchant Ruby Bolt", SpellCategory.ENCHANT),
    ENCHANT_DIAMOND_BOLT("Enchant Diamond Bolt", SpellCategory.ENCHANT),
    ENCHANT_DRAGONSTONE_BOLT("Enchant Dragonstone Bolt", SpellCategory.ENCHANT),
    ENCHANT_ONYX_BOLT("Enchant Onyx Bolt", SpellCategory.ENCHANT),

    // Standard - Charge orbs
    CHARGE_WATER_ORB("Charge Water Orb", SpellCategory.ORB),
    CHARGE_EARTH_ORB("Charge Earth Orb", SpellCategory.ORB),
    CHARGE_FIRE_ORB("Charge Fire Orb", SpellCategory.ORB),
    CHARGE_AIR_ORB("Charge Air Orb", SpellCategory.ORB),

    // Standard - Teleports
    VARROCK_TELEPORT("Varrock Teleport", SpellCategory.TELEPORT),
    LUMBRIDGE_TELEPORT("Lumbridge Teleport", SpellCategory.TELEPORT),
    FALADOR_TELEPORT("Falador Teleport", SpellCategory.TELEPORT),
    TELEPORT_TO_HOUSE("Teleport To House", SpellCategory.TELEPORT),
    CAMELOT_TELEPORT("Camelot Teleport", SpellCategory.TELEPORT),
    KOUREND_CASTLE_TELEPORT("Kourend Castle Teleport", SpellCategory.TELEPORT),
    ARDOUGNE_TELEPORT("Ardougne Teleport", SpellCategory.TELEPORT),
    CIVITAS_ILLA_FORTIS_TELEPORT("Civitas illa Fortis Teleport", SpellCategory.TELEPORT),
    WATCHTOWER_TELEPORT("Watchtower Teleport", SpellCategory.TELEPORT),
    TROLLHEIM_TELEPORT("Trollheim Teleport", SpellCategory.TELEPORT),
    TELEPORT_APE_ATOLL("Teleport Ape Atoll", SpellCategory.TELEPORT),
    TELEPORT_TO_TARGET("Teleport To Target", SpellCategory.TELEPORT),

    // Standard - Teleother
    TELEOTHER_LUMBRIDGE("Teleother Lumbridge", SpellCategory.TELEPORT),
    TELEOTHER_FALADOR("Teleother Falador", SpellCategory.TELEPORT),
    TELEOTHER_CAMELOT("Teleother Camelot", SpellCategory.TELEPORT),

    // Standard - Other
    BONES_TO_BANANAS("Bones To Bananas", SpellCategory.NONE),
    BONES_TO_PEACHES("Bones To Peaches", SpellCategory.NONE),
    TELEKINETIC_GRAB("Telekinetic Grab", SpellCategory.NONE),
    SUPERHEAT_ITEM("Superheat Item", SpellCategory.NONE),
    IBAN_BLAST("Iban Blast", SpellCategory.NONE),
    MAGIC_DART("Magic Dart", SpellCategory.NONE),
    CHARGE("Charge", SpellCategory.NONE),
    CRUMBLE_UNDEAD("Crumble Undead", SpellCategory.NONE),

    // Ancient - Smoke
    SMOKE_RUSH("Smoke Rush", SpellCategory.SMOKE),
    SMOKE_BURST("Smoke Burst", SpellCategory.SMOKE),
    SMOKE_BLITZ("Smoke Blitz", SpellCategory.SMOKE),
    SMOKE_BARRAGE("Smoke Barrage", SpellCategory.SMOKE),

    // Ancient - Shadow
    SHADOW_RUSH("Shadow Rush", SpellCategory.SHADOW),
    SHADOW_BURST("Shadow Burst", SpellCategory.SHADOW),
    SHADOW_BLITZ("Shadow Blitz", SpellCategory.SHADOW),
    SHADOW_BARRAGE("Shadow Barrage", SpellCategory.SHADOW),

    // Ancient - Blood
    BLOOD_RUSH("Blood Rush", SpellCategory.BLOOD),
    BLOOD_BURST("Blood Burst", SpellCategory.BLOOD),
    BLOOD_BLITZ("Blood Blitz", SpellCategory.BLOOD),
    BLOOD_BARRAGE("Blood Barrage", SpellCategory.BLOOD),

    // Ancient - Ice
    ICE_RUSH("Ice Rush", SpellCategory.ICE),
    ICE_BURST("Ice Burst", SpellCategory.ICE),
    ICE_BLITZ("Ice Blitz", SpellCategory.ICE),
    ICE_BARRAGE("Ice Barrage", SpellCategory.ICE),

    // Ancient - Teleports
    PADDEWWA_TELEPORT("Paddewwa Teleport", SpellCategory.TELEPORT),
    SENNTISTEN_TELEPORT("Senntisten Teleport", SpellCategory.TELEPORT),
    KHARYRLL_TELEPORT("Kharyrll Teleport", SpellCategory.TELEPORT),
    LASSAR_TELEPORT("Lassar Teleport", SpellCategory.TELEPORT),
    DAREEYAK_TELEPORT("Dareeyak Teleport", SpellCategory.TELEPORT),
    CARRALLANGER_TELEPORT("Carrallanger Teleport", SpellCategory.TELEPORT),
    ANNAKARL_TELEPORT("Annakarl Teleport", SpellCategory.TELEPORT),
    GHORROCK_TELEPORT("Ghorrock Teleport", SpellCategory.TELEPORT),

    // Arceuus - Grasp
    GHOSTLY_GRASP("Ghostly Grasp", SpellCategory.GRASP),
    SKELETAL_GRASP("Skeletal Grasp", SpellCategory.GRASP),
    UNDEAD_GRASP("Undead Grasp", SpellCategory.GRASP),

    // Arceuus - Demonbane
    INFERIOR_DEMONBANE("Inferior Demonbane", SpellCategory.DEMONBANE),
    SUPERIOR_DEMONBANE("Superior Demonbane", SpellCategory.DEMONBANE),
    DARK_DEMONBANE("Dark Demonbane", SpellCategory.DEMONBANE),

    // Arceuus - Thralls
    RESURRECT_LESSER_THRALL("Resurrect Lesser Thrall", SpellCategory.THRALL),
    RESURRECT_SUPERIOR_THRALL("Resurrect Superior Thrall", SpellCategory.THRALL),
    RESURRECT_GREATER_THRALL("Resurrect Greater Thrall", SpellCategory.THRALL),

    // Arceuus - Reanimation
    BASIC_REANIMATION("Basic Reanimation", SpellCategory.REANIMATE),
    ADEPT_REANIMATION("Adept Reanimation", SpellCategory.REANIMATE),
    EXPERT_REANIMATION("Expert Reanimation", SpellCategory.REANIMATE),
    MASTER_REANIMATION("Master Reanimation", SpellCategory.REANIMATE),

    // Arceuus - Teleports
    ARCEUUS_LIBRARY_TELEPORT("Arceuus Library Teleport", SpellCategory.TELEPORT),
    DRAYNOR_MANOR_TELEPORT("Draynor Manor Teleport", SpellCategory.TELEPORT),
    MIND_ALTAR_TELEPORT("Mind Altar Teleport", SpellCategory.TELEPORT),
    RESPAWN_TELEPORT("Respawn Teleport", SpellCategory.TELEPORT),
    SALVE_GRAVEYARD_TELEPORT("Salve Graveyard Teleport", SpellCategory.TELEPORT),
    FENKENSTRAINS_CASTLE_TELEPORT("Fenkenstrain's Castle Teleport", SpellCategory.TELEPORT),
    WEST_ARDOUGNE_TELEPORT("West Ardougne Teleport", SpellCategory.TELEPORT),
    HARMONY_ISLAND_TELEPORT("Harmony Island Teleport", SpellCategory.TELEPORT),
    CEMETERY_TELEPORT("Cemetery Teleport", SpellCategory.TELEPORT),
    BARROWS_TELEPORT("Barrows Teleport", SpellCategory.TELEPORT),
    APE_ATOLL_TELEPORT("Ape Atoll Teleport", SpellCategory.TELEPORT),

    // Arceuus - Other
    MARK_OF_DARKNESS("Mark of Darkness", SpellCategory.NONE),
    WARD_OF_ARCEUUS("Ward of Arceuus", SpellCategory.NONE),
    DEATH_CHARGE("Death Charge", SpellCategory.DEATH_CHARGE),
    SHADOW_VEIL("Shadow Veil", SpellCategory.NONE),
    DARK_LURE("Dark Lure", SpellCategory.NONE),
    LESSER_CORRUPTION("Lesser Corruption", SpellCategory.NONE),
    GREATER_CORRUPTION("Greater Corruption", SpellCategory.NONE),
    VILE_VIGOUR("Vile Vigour", SpellCategory.NONE),
    DEGRIME("Degrime", SpellCategory.NONE),
    RESURRECT_CROPS("Resurrect Crops", SpellCategory.NONE),
    DEMONIC_OFFERING("Demonic Offering", SpellCategory.NONE),
    SINISTER_OFFERING("Sinister Offering", SpellCategory.NONE),

    // Lunar - Teleports
    MOONCLAN_TELEPORT("Moonclan Teleport", SpellCategory.TELEPORT),
    OURANIA_TELEPORT("Ourania Teleport", SpellCategory.TELEPORT),
    WATERBIRTH_TELEPORT("Waterbirth Teleport", SpellCategory.TELEPORT),
    BARBARIAN_TELEPORT("Barbarian Teleport", SpellCategory.TELEPORT),
    KHAZARD_TELEPORT("Khazard Teleport", SpellCategory.TELEPORT),
    FISHING_GUILD_TELEPORT("Fishing Guild Teleport", SpellCategory.TELEPORT),
    CATHERBY_TELEPORT("Catherby Teleport", SpellCategory.TELEPORT),
    ICE_PLATEAU_TELEPORT("Ice Plateau Teleport", SpellCategory.TELEPORT),

    // Lunar - Group teleports
    TELE_GROUP_MOONCLAN("Tele Group Moonclan", SpellCategory.TELEPORT),
    TELE_GROUP_WATERBIRTH("Tele Group Waterbirth", SpellCategory.TELEPORT),
    TELE_GROUP_BARBARIAN("Tele Group Barbarian", SpellCategory.TELEPORT),
    TELE_GROUP_KHAZARD("Tele Group Khazard", SpellCategory.TELEPORT),
    TELE_GROUP_FISHING_GUILD("Tele Group Fishing Guild", SpellCategory.TELEPORT),
    TELE_GROUP_CATHERBY("Tele Group Catherby", SpellCategory.TELEPORT),
    TELE_GROUP_ICE_PLATEAU("Tele Group Ice Plateau", SpellCategory.TELEPORT),

    // Lunar - Other
    BAKE_PIE("Bake Pie", SpellCategory.NONE),
    GEOMANCY("Geomancy", SpellCategory.NONE),
    CURE_PLANT("Cure Plant", SpellCategory.NONE),
    MONSTER_EXAMINE("Monster Examine", SpellCategory.NONE),
    NPC_CONTACT("Npc Contact", SpellCategory.NONE),
    CURE_OTHER("Cure Other", SpellCategory.NONE),
    HUMIDIFY("Humidify", SpellCategory.NONE),
    CURE_ME("Cure Me", SpellCategory.NONE),
    HUNTER_KIT("Hunter Kit", SpellCategory.NONE),
    CURE_GROUP("Cure Group", SpellCategory.NONE),
    STAT_SPY("Stat Spy", SpellCategory.NONE),
    SPIN_FLAX("Spin Flax", SpellCategory.NONE),
    SUPERGLASS_MAKE("Superglass Make", SpellCategory.NONE),
    TAN_LEATHER("Tan Leather", SpellCategory.NONE),
    DREAM("Dream", SpellCategory.NONE),
    STRING_JEWELLERY("String Jewellery", SpellCategory.NONE),
    STAT_RESTORE_POT_SHARE("Stat Restore Pot Share", SpellCategory.NONE),
    MAGIC_IMBUE("Magic Imbue", SpellCategory.NONE),
    FERTILE_SOIL("Fertile Soil", SpellCategory.NONE),
    BOOST_POTION_SHARE("Boost Potion Share", SpellCategory.NONE),
    PLANK_MAKE("Plank Make", SpellCategory.NONE),
    RECHARGE_DRAGONSTONE("Recharge Dragonstone", SpellCategory.NONE),
    ENERGY_TRANSFER("Energy Transfer", SpellCategory.NONE),
    HEAL_OTHER("Heal Other", SpellCategory.NONE),
    VENGEANCE_OTHER("Vengeance Other", SpellCategory.NONE),
    VENGEANCE("Vengeance", SpellCategory.NONE),
    HEAL_GROUP("Heal Group", SpellCategory.NONE),
    SPELLBOOK_SWAP("Spellbook Swap", SpellCategory.NONE);

    private final String name;
    private final SpellCategory spellCategory;

    private static final ImmutableMap<String, Spell> BY_NAME = Arrays.stream(values())
            .collect(toImmutableMap(s -> s.name.toLowerCase(Locale.ROOT), Function.identity()));

    /**
     * Case-insensitive exact name match, or UNKNOWN.
     */
    public static Spell fromString(String name) {
        if (name == null) {
            return UNKNOWN;
        }

        return BY_NAME.getOrDefault(name.trim().toLowerCase(Locale.ROOT), UNKNOWN);
    }


    public boolean isUnknown() {
        return this == UNKNOWN;
    }
}