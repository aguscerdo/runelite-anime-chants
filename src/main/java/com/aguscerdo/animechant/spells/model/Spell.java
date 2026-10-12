package com.aguscerdo.animechant.spells.model;

import com.aguscerdo.animechant.chantdata.TextEntry;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;
import java.util.Optional;

/**
 * Known spells and the category they belong to. A spell that isn't listed here can
 * still get text through a spell-name entry in a preset/override.
 */
@AllArgsConstructor
@Getter
public enum Spell {
    UNKNOWN("Unknown", SpellCategory.UNKNOWN, SpellCastType.UNKNOWN, ImmutableList.of()),
    // Home teleport 4847

    // Standard - Fire
    FIRE_STRIKE("Fire Strike", SpellCategory.FIRE, SpellCastType.WORLD_TARGET, ImmutableList.of(711, 1162, 9144, 11423)),
    FIRE_BOLT("Fire Bolt", SpellCategory.FIRE, SpellCastType.WORLD_TARGET, ImmutableList.of(711, 1162, 9144, 11423)),
    FIRE_BLAST("Fire Blast", SpellCategory.FIRE, SpellCastType.WORLD_TARGET, ImmutableList.of(711, 1162, 9144, 11423)),
    FIRE_WAVE("Fire Wave", SpellCategory.FIRE, SpellCastType.WORLD_TARGET, ImmutableList.of(727, 1167, 11429, 11430)),
    FIRE_SURGE("Fire Surge", SpellCategory.FIRE, SpellCastType.WORLD_TARGET, ImmutableList.of(7855, 9145, 11431)),

    // Standard - Air
    WIND_STRIKE("Wind Strike", SpellCategory.AIR, SpellCastType.WORLD_TARGET, ImmutableList.of(711, 1162, 9144, 11423)),
    WIND_BOLT("Wind Bolt", SpellCategory.AIR, SpellCastType.WORLD_TARGET, ImmutableList.of(711, 1162, 9144, 11423)),
    WIND_BLAST("Wind Blast", SpellCategory.AIR, SpellCastType.WORLD_TARGET, ImmutableList.of(711, 1162, 9144, 11423)),
    WIND_WAVE("Wind Wave", SpellCategory.AIR, SpellCastType.WORLD_TARGET, ImmutableList.of(727, 1167, 11429, 11430)),
    WIND_SURGE("Wind Surge", SpellCategory.AIR, SpellCastType.WORLD_TARGET, ImmutableList.of(7855, 9145, 11431)),

    // Standard - Water
    WATER_STRIKE("Water Strike", SpellCategory.WATER, SpellCastType.WORLD_TARGET, ImmutableList.of(711, 1162, 9144, 11423)),
    WATER_BOLT("Water Bolt", SpellCategory.WATER, SpellCastType.WORLD_TARGET, ImmutableList.of(711, 1162, 9144, 11423)),
    WATER_BLAST("Water Blast", SpellCategory.WATER, SpellCastType.WORLD_TARGET, ImmutableList.of(711, 1162, 9144, 11423)),
    WATER_WAVE("Water Wave", SpellCategory.WATER, SpellCastType.WORLD_TARGET, ImmutableList.of(727, 1167, 11429, 11430)),
    WATER_SURGE("Water Surge", SpellCategory.WATER, SpellCastType.WORLD_TARGET, ImmutableList.of(7855, 9145, 11431)),

    // Standard - Earth
    EARTH_STRIKE("Earth Strike", SpellCategory.EARTH, SpellCastType.WORLD_TARGET, ImmutableList.of(711, 1162, 9144, 11423)),
    EARTH_BOLT("Earth Bolt", SpellCategory.EARTH, SpellCastType.WORLD_TARGET, ImmutableList.of(711, 1162, 9144, 11423)),
    EARTH_BLAST("Earth Blast", SpellCategory.EARTH, SpellCastType.WORLD_TARGET, ImmutableList.of(711, 1162, 9144, 11423)),
    EARTH_WAVE("Earth Wave", SpellCategory.EARTH, SpellCastType.WORLD_TARGET, ImmutableList.of(727, 1167, 11429, 11430)),
    EARTH_SURGE("Earth Surge", SpellCategory.EARTH, SpellCastType.WORLD_TARGET, ImmutableList.of(7855, 9145, 11431)),


    // Standard - God spells
    FLAMES_OF_ZAMORAK("Flames Of Zamorak", SpellCategory.GOD, SpellCastType.WORLD_TARGET, ImmutableList.of(811)),
    CLAWS_OF_GUTHIX("Claws Of Guthix", SpellCategory.GOD, SpellCastType.WORLD_TARGET, ImmutableList.of(811)),
    SARADOMIN_STRIKE("Saradomin Strike", SpellCategory.GOD, SpellCastType.WORLD_TARGET, ImmutableList.of(811)),
    CHARGE("Charge", SpellCategory.GOD, SpellCastType.INSTANT, ImmutableList.of(811)),

    // Standard - Bind
    BIND("Bind", SpellCategory.BIND, SpellCastType.WORLD_TARGET, ImmutableList.of(710, 1161)),
    SNARE("Snare", SpellCategory.BIND, SpellCastType.WORLD_TARGET, ImmutableList.of(710, 1161)),
    ENTANGLE("Entangle", SpellCategory.BIND, SpellCastType.WORLD_TARGET, ImmutableList.of(710, 1161)),

    // Standard - Curses / debuffs
    CONFUSE("Confuse", SpellCategory.CURSE, SpellCastType.WORLD_TARGET, ImmutableList.of(716, 1163, 11424, 11425)),
    WEAKEN("Weaken", SpellCategory.CURSE, SpellCastType.WORLD_TARGET, ImmutableList.of(717, 1164, 11426, 11427)),
    CURSE("Curse", SpellCategory.CURSE, SpellCastType.WORLD_TARGET, ImmutableList.of(718, 1165, 10413, 11428)),
    VULNERABILITY("Vulnerability", SpellCategory.CURSE, SpellCastType.WORLD_TARGET, ImmutableList.of(718, 1165, 10413, 11428)),
    ENFEEBLE("Enfeeble", SpellCategory.CURSE, SpellCastType.WORLD_TARGET, ImmutableList.of(727, 1168)),
    STUN("Stun", SpellCategory.CURSE, SpellCastType.WORLD_TARGET, ImmutableList.of(729, 1169)),
    TELE_BLOCK("Tele Block", SpellCategory.CURSE, SpellCastType.WORLD_TARGET, ImmutableList.of(1819, 1820, 7754, 10093)), // 793 teleblock?

    // Standard - Alchemy
    LOW_LEVEL_ALCHEMY("Low Level Alchemy", SpellCategory.ALCHEMY, SpellCastType.INVENTORY_TARGET, ImmutableList.of(712, 10623)),
    HIGH_LEVEL_ALCHEMY("High Level Alchemy", SpellCategory.ALCHEMY, SpellCastType.INVENTORY_TARGET, ImmutableList.of(713, 10624)),

    // Standard - Enchant (jewellery)
    LVL_1_ENCHANT("Lvl-1 Enchant", SpellCategory.ENCHANT, SpellCastType.INVENTORY_TARGET, ImmutableList.of(931)),
    LVL_2_ENCHANT("Lvl-2 Enchant", SpellCategory.ENCHANT, SpellCastType.INVENTORY_TARGET, ImmutableList.of(931)),
    LVL_3_ENCHANT("Lvl-3 Enchant", SpellCategory.ENCHANT, SpellCastType.INVENTORY_TARGET, ImmutableList.of(931)),
    LVL_4_ENCHANT("Lvl-4 Enchant", SpellCategory.ENCHANT, SpellCastType.INVENTORY_TARGET, ImmutableList.of(931)),
    LVL_5_ENCHANT("Lvl-5 Enchant", SpellCategory.ENCHANT, SpellCastType.INVENTORY_TARGET, ImmutableList.of(931)),
    LVL_6_ENCHANT("Lvl-6 Enchant", SpellCategory.ENCHANT, SpellCastType.INVENTORY_TARGET, ImmutableList.of(931)),
    LVL_7_ENCHANT("Lvl-7 Enchant", SpellCategory.ENCHANT, SpellCastType.INVENTORY_TARGET, ImmutableList.of(931)),

    // Standard - Enchant (crossbow bolts)
    // TODO handle menu Make-x Actions separately.
    ENCHANT_OPAL_BOLT("Enchant Opal Bolt", SpellCategory.ENCHANT, SpellCastType.INVENTORY_TARGET, ImmutableList.of(931)),
    ENCHANT_SAPPHIRE_BOLT("Enchant Sapphire Bolt", SpellCategory.ENCHANT, SpellCastType.INVENTORY_TARGET, ImmutableList.of(931)),
    ENCHANT_JADE_BOLT("Enchant Jade Bolt", SpellCategory.ENCHANT, SpellCastType.INVENTORY_TARGET, ImmutableList.of(931)),
    ENCHANT_PEARL_BOLT("Enchant Pearl Bolt", SpellCategory.ENCHANT, SpellCastType.INVENTORY_TARGET, ImmutableList.of(931)),
    ENCHANT_EMERALD_BOLT("Enchant Emerald Bolt", SpellCategory.ENCHANT, SpellCastType.INVENTORY_TARGET, ImmutableList.of(931)),
    ENCHANT_TOPAZ_BOLT("Enchant Topaz Bolt", SpellCategory.ENCHANT, SpellCastType.INVENTORY_TARGET, ImmutableList.of(931)),
    ENCHANT_RUBY_BOLT("Enchant Ruby Bolt", SpellCategory.ENCHANT, SpellCastType.INVENTORY_TARGET, ImmutableList.of(931)),
    ENCHANT_DIAMOND_BOLT("Enchant Diamond Bolt", SpellCategory.ENCHANT, SpellCastType.INVENTORY_TARGET, ImmutableList.of(931)),
    ENCHANT_DRAGONSTONE_BOLT("Enchant Dragonstone Bolt", SpellCategory.ENCHANT, SpellCastType.INVENTORY_TARGET, ImmutableList.of(931)),
    ENCHANT_ONYX_BOLT("Enchant Onyx Bolt", SpellCategory.ENCHANT, SpellCastType.INVENTORY_TARGET, ImmutableList.of(931)),

    // Standard - Charge orbs
    CHARGE_WATER_ORB("Charge Water Orb", SpellCategory.CHARGE_ORB, SpellCastType.WORLD_TARGET, ImmutableList.of(726)),
    CHARGE_EARTH_ORB("Charge Earth Orb", SpellCategory.CHARGE_ORB, SpellCastType.WORLD_TARGET, ImmutableList.of(726)),
    CHARGE_FIRE_ORB("Charge Fire Orb", SpellCategory.CHARGE_ORB, SpellCastType.WORLD_TARGET, ImmutableList.of(726)),
    CHARGE_AIR_ORB("Charge Air Orb", SpellCategory.CHARGE_ORB, SpellCastType.WORLD_TARGET, ImmutableList.of(726)),

    // Standard - Teleports
    VARROCK_TELEPORT("Varrock Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(714, 715)),
    GRAND_EXCHANGE_TELEPORT("Grand Exchange Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(714, 715)),
    LUMBRIDGE_TELEPORT("Lumbridge Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(714, 715)),
    FALADOR_TELEPORT("Falador Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(714, 715)),
    TELEPORT_TO_HOUSE("Teleport To House", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(714, 715)),
    CAMELOT_TELEPORT("Camelot Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(714, 715)),
    KOUREND_CASTLE_TELEPORT("Kourend Castle Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(714, 715)),
    ARDOUGNE_TELEPORT("Ardougne Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(714, 715)),
    CIVITAS_ILLA_FORTIS_TELEPORT("Civitas illa Fortis Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(714, 715)),
    WATCHTOWER_TELEPORT("Watchtower Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(714, 715)),
    YANILLE_TELEPORT("Yanille Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(714, 715)),
    TROLLHEIM_TELEPORT("Trollheim Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(714, 715)),
    TELEPORT_TO_TARGET("Teleport To Target", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(714, 715)),

    // Handles both Arceuus and Standard Teleport
    APE_ATOLL_TELEPORT("Ape Atoll Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(714, 715, 3865, 6575)),

    // Standard - Teleother
    TELEOTHER_LUMBRIDGE("Teleother Lumbridge", SpellCategory.TELE_OTHER, SpellCastType.WORLD_TARGET, ImmutableList.of(1818)),
    TELEOTHER_FALADOR("Teleother Falador", SpellCategory.TELE_OTHER, SpellCastType.WORLD_TARGET, ImmutableList.of(1818)),
    TELEOTHER_CAMELOT("Teleother Camelot", SpellCategory.TELE_OTHER, SpellCastType.WORLD_TARGET, ImmutableList.of(1818)),

    // Standard - Other
    BONES_TO_BANANAS("Bones To Bananas", SpellCategory.UNKNOWN, SpellCastType.INSTANT, ImmutableList.of(722)),
    BONES_TO_PEACHES("Bones To Peaches", SpellCategory.UNKNOWN, SpellCastType.INSTANT, ImmutableList.of(722)),
    TELEKINETIC_GRAB("Telekinetic Grab", SpellCategory.UNKNOWN, SpellCastType.WORLD_TARGET, ImmutableList.of(723)),
    SUPERHEAT_ITEM("Superheat Item", SpellCategory.UNKNOWN, SpellCastType.INVENTORY_TARGET, ImmutableList.of(725)),
    IBAN_BLAST("Iban Blast", SpellCategory.UNKNOWN, SpellCastType.WORLD_TARGET, ImmutableList.of(708)),
    MAGIC_DART("Magic Dart", SpellCategory.UNKNOWN, SpellCastType.INVENTORY_TARGET, ImmutableList.of(811)),
    CRUMBLE_UNDEAD("Crumble Undead", SpellCategory.UNKNOWN, SpellCastType.WORLD_TARGET, ImmutableList.of(724, 1166)),

    // Ancient - Smoke
    SMOKE_RUSH("Smoke Rush", SpellCategory.SMOKE, SpellCastType.INVENTORY_TARGET, ImmutableList.of(1978, 10091)),
    SMOKE_BURST("Smoke Burst", SpellCategory.SMOKE, SpellCastType.INVENTORY_TARGET, ImmutableList.of(1979, 10092)),
    SMOKE_BLITZ("Smoke Blitz", SpellCategory.SMOKE, SpellCastType.INVENTORY_TARGET, ImmutableList.of(1978, 10091)),
    SMOKE_BARRAGE("Smoke Barrage", SpellCategory.SMOKE, SpellCastType.INVENTORY_TARGET, ImmutableList.of(1979, 10092)),

    // Ancient - Shadow
    SHADOW_RUSH("Shadow Rush", SpellCategory.SHADOW, SpellCastType.INVENTORY_TARGET, ImmutableList.of(1978, 10091)),
    SHADOW_BURST("Shadow Burst", SpellCategory.SHADOW, SpellCastType.INVENTORY_TARGET, ImmutableList.of(1979, 10092)),
    SHADOW_BLITZ("Shadow Blitz", SpellCategory.SHADOW, SpellCastType.INVENTORY_TARGET, ImmutableList.of(1978, 10091)),
    SHADOW_BARRAGE("Shadow Barrage", SpellCategory.SHADOW, SpellCastType.INVENTORY_TARGET, ImmutableList.of(1979, 10092)),

    // Ancient - Blood
    BLOOD_RUSH("Blood Rush", SpellCategory.BLOOD, SpellCastType.INVENTORY_TARGET, ImmutableList.of(1978, 10091)),
    BLOOD_BURST("Blood Burst", SpellCategory.BLOOD, SpellCastType.INVENTORY_TARGET, ImmutableList.of(1979, 10092)),
    BLOOD_BLITZ("Blood Blitz", SpellCategory.BLOOD, SpellCastType.INVENTORY_TARGET, ImmutableList.of(1978, 10091)),
    BLOOD_BARRAGE("Blood Barrage", SpellCategory.BLOOD, SpellCastType.INVENTORY_TARGET, ImmutableList.of(1979, 10092)),

    // Ancient - Ice
    ICE_RUSH("Ice Rush", SpellCategory.ICE, SpellCastType.INVENTORY_TARGET, ImmutableList.of(1978, 10091)),
    ICE_BURST("Ice Burst", SpellCategory.ICE, SpellCastType.INVENTORY_TARGET, ImmutableList.of(1979, 10092)),
    ICE_BLITZ("Ice Blitz", SpellCategory.ICE, SpellCastType.INVENTORY_TARGET, ImmutableList.of(1978, 10091)),
    ICE_BARRAGE("Ice Barrage", SpellCategory.ICE, SpellCastType.INVENTORY_TARGET, ImmutableList.of(1979, 10092)),

    // Ancient - Teleports
    PADDEWWA_TELEPORT("Paddewwa Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(1988)),
    SENNTISTEN_TELEPORT("Senntisten Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(1988)),
    KHARYRLL_TELEPORT("Kharyrll Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(1988)),
    LASSAR_TELEPORT("Lassar Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(1988)),
    DAREEYAK_TELEPORT("Dareeyak Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(1988)),
    CARRALLANGER_TELEPORT("Carrallanger Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(1988)),
    ANNAKARL_TELEPORT("Annakarl Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(1988)),
    GHORROCK_TELEPORT("Ghorrock Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(1988)),

    // Arceuus - Grasp
    GHOSTLY_GRASP("Ghostly Grasp", SpellCategory.GRASP, SpellCastType.WORLD_TARGET, ImmutableList.of(8972)),
    SKELETAL_GRASP("Skeletal Grasp", SpellCategory.GRASP, SpellCastType.WORLD_TARGET, ImmutableList.of(8972)),
    UNDEAD_GRASP("Undead Grasp", SpellCategory.GRASP, SpellCastType.WORLD_TARGET, ImmutableList.of(8972)),

    // Arceuus - Demonbane
    INFERIOR_DEMONBANE("Inferior Demonbane", SpellCategory.DEMONBANE, SpellCastType.WORLD_TARGET, ImmutableList.of(8977)),
    SUPERIOR_DEMONBANE("Superior Demonbane", SpellCategory.DEMONBANE, SpellCastType.WORLD_TARGET, ImmutableList.of(8977)),
    DARK_DEMONBANE("Dark Demonbane", SpellCategory.DEMONBANE, SpellCastType.WORLD_TARGET, ImmutableList.of(8977)),

    // Arceuus - Thralls
    RESURRECT_LESSER_THRALL("Resurrect Lesser Thrall", SpellCategory.THRALL, SpellCastType.INSTANT, ImmutableList.of(8973)),
    RESURRECT_SUPERIOR_THRALL("Resurrect Superior Thrall", SpellCategory.THRALL, SpellCastType.INSTANT, ImmutableList.of(8973)),
    RESURRECT_GREATER_THRALL("Resurrect Greater Thrall", SpellCategory.THRALL, SpellCastType.INSTANT, ImmutableList.of(8973)),

    // Arceuus - Reanimation
    BASIC_REANIMATION("Basic Reanimation", SpellCategory.REANIMATE, SpellCastType.INVENTORY_TARGET, ImmutableList.of(7198)),
    ADEPT_REANIMATION("Adept Reanimation", SpellCategory.REANIMATE, SpellCastType.INVENTORY_TARGET, ImmutableList.of(7198)),
    EXPERT_REANIMATION("Expert Reanimation", SpellCategory.REANIMATE, SpellCastType.INVENTORY_TARGET, ImmutableList.of(7198)),
    MASTER_REANIMATION("Master Reanimation", SpellCategory.REANIMATE, SpellCastType.INVENTORY_TARGET, ImmutableList.of(7198)),

    // Arceuus - Teleports
    ARCEUUS_LIBRARY_TELEPORT("Arceuus Library Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(3865, 6575)),
    DRAYNOR_MANOR_TELEPORT("Draynor Manor Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(3865, 6575)),
    MIND_ALTAR_TELEPORT("Mind Altar Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(3865, 6575)),
    RESPAWN_TELEPORT("Respawn Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(3865, 6575)),
    SALVE_GRAVEYARD_TELEPORT("Salve Graveyard Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(3865, 6575)),
    FENKENSTRAINS_CASTLE_TELEPORT("Fenkenstrain's Castle Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(3865, 6575)),
    WEST_ARDOUGNE_TELEPORT("West Ardougne Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(3865, 6575)),
    HARMONY_ISLAND_TELEPORT("Harmony Island Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(3865, 6575)),
    CEMETERY_TELEPORT("Cemetery Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(3865, 6575)),
    BARROWS_TELEPORT("Barrows Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(3865, 6575)),
    // APE_ATOLL_TELEPORT("Ape Atoll Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(3865, 6575)),

    // Arceuus - Offering
    RESURRECT_CROPS("Resurrect Crops", SpellCategory.OFFERING, SpellCastType.WORLD_TARGET, ImmutableList.of(7163)),
    DEMONIC_OFFERING("Demonic Offering", SpellCategory.OFFERING, SpellCastType.INSTANT, ImmutableList.of(8975)),
    SINISTER_OFFERING("Sinister Offering", SpellCategory.OFFERING, SpellCastType.INSTANT, ImmutableList.of(8975)),

    // Arceuus - Other
    MARK_OF_DARKNESS("Mark of Darkness", SpellCategory.UNKNOWN, SpellCastType.INSTANT, ImmutableList.of(8970)),
    WARD_OF_ARCEUUS("Ward of Arceuus", SpellCategory.UNKNOWN, SpellCastType.INSTANT, ImmutableList.of(8970)),
    DEATH_CHARGE("Death Charge", SpellCategory.UNKNOWN, SpellCastType.INSTANT, ImmutableList.of(8970)),
    SHADOW_VEIL("Shadow Veil", SpellCategory.UNKNOWN, SpellCastType.INSTANT, ImmutableList.of(8979)),
    DARK_LURE("Dark Lure", SpellCategory.UNKNOWN, SpellCastType.WORLD_TARGET, ImmutableList.of(8974)),
    LESSER_CORRUPTION("Lesser Corruption", SpellCategory.CORRUPTION, SpellCastType.INSTANT, ImmutableList.of(8977)),
    GREATER_CORRUPTION("Greater Corruption", SpellCategory.CORRUPTION, SpellCastType.INSTANT, ImmutableList.of(8977)),
    VILE_VIGOUR("Vile Vigour", SpellCategory.UNKNOWN, SpellCastType.INSTANT, ImmutableList.of(8978)),
    DEGRIME("Degrime", SpellCategory.UNKNOWN, SpellCastType.INSTANT, ImmutableList.of(8980)),


    // Lunar - Teleports
    MOONCLAN_TELEPORT("Moonclan Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(1816)),
    OURANIA_TELEPORT("Ourania Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(1816)),
    WATERBIRTH_TELEPORT("Waterbirth Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(1816)),
    BARBARIAN_TELEPORT("Barbarian Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(1816)),
    KHAZARD_TELEPORT("Khazard Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(1816)),
    FISHING_GUILD_TELEPORT("Fishing Guild Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(1816)),
    CATHERBY_TELEPORT("Catherby Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(1816)),
    ICE_PLATEAU_TELEPORT("Ice Plateau Teleport", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(1816)),

    // Lunar - Group teleports
    TELE_GROUP_MOONCLAN("Tele Group Moonclan", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(1816)),
    TELE_GROUP_WATERBIRTH("Tele Group Waterbirth", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(1816)),
    TELE_GROUP_BARBARIAN("Tele Group Barbarian", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(1816)),
    TELE_GROUP_KHAZARD("Tele Group Khazard", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(1816)),
    TELE_GROUP_FISHING_GUILD("Tele Group Fishing Guild", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(1816)),
    TELE_GROUP_CATHERBY("Tele Group Catherby", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(1816)),
    TELE_GROUP_ICE_PLATEAU("Tele Group Ice Plateau", SpellCategory.TELEPORT, SpellCastType.INSTANT, ImmutableList.of(1816)),

    // Lunar - Vengeance
    VENGEANCE("Vengeance", SpellCategory.VENGEANCE, SpellCastType.INSTANT, ImmutableList.of(8316, 8317, 10626)),
    VENGEANCE_OTHER("Vengeance Other", SpellCategory.VENGEANCE, SpellCastType.WORLD_TARGET, ImmutableList.of(8316, 8317, 10626)),

    // Lunar - Cure
    CURE_GROUP("Cure Group", SpellCategory.CURE, SpellCastType.INSTANT, ImmutableList.of(4411)),
    CURE_ME("Cure Me", SpellCategory.CURE, SpellCastType.INSTANT, ImmutableList.of(4411)),
    CURE_OTHER("Cure Other", SpellCategory.CURE, SpellCastType.WORLD_TARGET, ImmutableList.of(4411)),
    CURE_PLANT("Cure Plant", SpellCategory.CURE, SpellCastType.WORLD_TARGET, ImmutableList.of(4432)), // 4432?

    // Lunar - Heal
    HEAL_GROUP("Heal Group", SpellCategory.HEAL, SpellCastType.INSTANT, ImmutableList.of(709)),
    HEAL_OTHER("Heal Other", SpellCategory.HEAL, SpellCastType.WORLD_TARGET, ImmutableList.of(709)),

    // Lunar - Other
    ASTRAL_CONTACT("Astral Contact", SpellCategory.UNKNOWN, SpellCastType.INSTANT, ImmutableList.of(11904)),
    BAKE_PIE("Bake Pie", SpellCategory.UNKNOWN, SpellCastType.INSTANT, ImmutableList.of(4413)),
    BOOST_POTION_SHARE("Boost Potion Share", SpellCategory.UNKNOWN, SpellCastType.INVENTORY_TARGET, ImmutableList.of(4413)),
    DREAM("Dream", SpellCategory.UNKNOWN, SpellCastType.INSTANT, ImmutableList.of(7627)),
    ENERGY_TRANSFER("Energy Transfer", SpellCategory.UNKNOWN, SpellCastType.WORLD_TARGET, ImmutableList.of(4411)),
    FERTILE_SOIL("Fertile Soil", SpellCategory.UNKNOWN, SpellCastType.WORLD_TARGET, ImmutableList.of(4413)),
    GEOMANCY("Geomancy", SpellCategory.UNKNOWN, SpellCastType.INSTANT, ImmutableList.of(7118)),
    HUMIDIFY("Humidify", SpellCategory.UNKNOWN, SpellCastType.INSTANT, ImmutableList.of(6294)),
    HUNTER_KIT("Hunter Kit", SpellCategory.UNKNOWN, SpellCastType.INSTANT, ImmutableList.of(6303)),
    MAGIC_IMBUE("Magic Imbue", SpellCategory.UNKNOWN, SpellCastType.INSTANT, ImmutableList.of(722, 8970)),
    MONSTER_EXAMINE("Monster Examine", SpellCategory.UNKNOWN, SpellCastType.WORLD_TARGET, ImmutableList.of(6293, 6306)),
    PLANK_MAKE("Plank Make", SpellCategory.UNKNOWN, SpellCastType.INSTANT, ImmutableList.of(6298)),
    RECHARGE_DRAGONSTONE("Recharge Dragonstone", SpellCategory.ENCHANT, SpellCastType.INSTANT, ImmutableList.of(712)),
    SPELLBOOK_SWAP("Spellbook Swap", SpellCategory.UNKNOWN, SpellCastType.UNKNOWN, ImmutableList.of(6299)), // TODO handle menu
    SPIN_FLAX("Spin Flax", SpellCategory.UNKNOWN, SpellCastType.INSTANT, ImmutableList.of(4413)),
    STAT_RESTORE_POT_SHARE("Stat Restore Pot Share", SpellCategory.UNKNOWN, SpellCastType.INVENTORY_TARGET, ImmutableList.of(4413)),
    STAT_SPY("Stat Spy", SpellCategory.UNKNOWN, SpellCastType.WORLD_TARGET, ImmutableList.of(6293)),
    STRING_JEWELLERY("String Jewellery", SpellCategory.UNKNOWN, SpellCastType.INSTANT, ImmutableList.of(4412)),
    SUPERGLASS_MAKE("Superglass Make", SpellCategory.UNKNOWN, SpellCastType.INSTANT, ImmutableList.of(4413)),
    TAN_LEATHER("Tan Leather", SpellCategory.UNKNOWN, SpellCastType.INSTANT, ImmutableList.of(712));

    private final String name;
    private final SpellCategory spellCategory;
    private final SpellCastType spellCastType;
    private final ImmutableList<Integer> animationIds;

    // Must be kept in lower-case
    private static final ImmutableMap<String, Spell> ALTERNATIVE_SPELL_NAMES = ImmutableMap.<String, Spell>builder()
            .put("enchant sapphire", LVL_1_ENCHANT)
            .put("enchant emerald", LVL_2_ENCHANT)
            .put("enchant ruby", LVL_3_ENCHANT)
            .put("enchant diamond", LVL_4_ENCHANT)
            .put("enchant dragonstone", LVL_5_ENCHANT)
            .put("enchant onyx", LVL_6_ENCHANT)
            .put("enchant zenyte", LVL_7_ENCHANT)
            .put("npc contact", ASTRAL_CONTACT)
            .build();


    private static final ImmutableMap<String, Spell> SPELLS_BY_NAME;

    @VisibleForTesting
    static final int WORLD_TARGETED_TIMEOUT_TICKS = 30;
    @VisibleForTesting
    public static final int INSTANT_TIMEOUT_TICKS = 6;

    static {
        ImmutableMap.Builder<String, Spell> spellsByName = ImmutableMap.builder();

        for (Spell spell : values()) {
            spellsByName.put(spell.name.toLowerCase(Locale.ROOT), spell);
        }

        spellsByName.putAll(ALTERNATIVE_SPELL_NAMES);

        SPELLS_BY_NAME = spellsByName.build();
    }

    /**
     * Case-insensitive exact name match, or UNKNOWN.
     */
    public static Spell fromString(String name) {
        if (name == null || name.isEmpty()) {
            return UNKNOWN;
        }

        return SPELLS_BY_NAME.getOrDefault(name.trim().toLowerCase(Locale.ROOT), UNKNOWN);
    }

    public TextEntry asTextEntry() {
        return new TextEntry(name, Optional.empty());
    }

    public boolean animationIdMatches(int animationId) {
        return animationIds.contains(animationId);
    }

    public int timeoutTicks() {
        // Spells that may drag the player. Inventory spells trigger immediately when used on items.
        if (spellCastType == SpellCastType.WORLD_TARGET) {
            return WORLD_TARGETED_TIMEOUT_TICKS;
        }

        return INSTANT_TIMEOUT_TICKS;
    }

    public boolean isUnknown() {
        return this == UNKNOWN;
    }
}