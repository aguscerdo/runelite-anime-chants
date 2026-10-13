package com.aguscerdo.animechant.specials.model;

import com.aguscerdo.animechant.chantdata.model.TextEntry;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.gameval.ItemID;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

@AllArgsConstructor
@Getter
public enum SpecWeapon {
    UNKNOWN("Unknown", "Unknown", ImmutableList.of()),

    DRAGON_AXE("Dragon axe", "Lumber Up", ImmutableList.of(
            ItemID.DRAGON_AXE,
            ItemID.TRAILBLAZER_AXE_NO_INFERNAL,
            ItemID.TRAILBLAZER_RELOADED_AXE_NO_INFERNAL)),
    DRAGON_FELLING_AXE("Dragon felling axe", "Lumber Up", ImmutableList.of(
            ItemID.DRAGON_AXE_2H)),
    INFERNAL_AXE("Infernal axe", "Lumber Up", ImmutableList.of(
            ItemID.INFERNAL_AXE,
            ItemID.INFERNAL_AXE_EMPTY,
            ItemID.TRAILBLAZER_AXE,
            ItemID.TRAILBLAZER_AXE_EMPTY,
            ItemID.TRAILBLAZER_RELOADED_AXE,
            ItemID.TRAILBLAZER_RELOADED_AXE_EMPTY)),
    THIRD_AGE_AXE("3rd Age axe", "Lumber Up", ImmutableList.of(
            ItemID._3A_AXE)),
    THIRD_AGE_FELLING_AXE("3rd Age felling axe", "Lumber Up", ImmutableList.of(
            ItemID._3A_AXE_2H)),
    CRYSTAL_AXE("Crystal axe", "Lumber Up", ImmutableList.of(
            ItemID.CRYSTAL_AXE,
            ItemID.CRYSTAL_AXE_INACTIVE)),
    CRYSTAL_FELLING_AXE("Crystal felling axe", "Lumber Up", ImmutableList.of(
            ItemID.CRYSTAL_AXE_2H,
            ItemID.CRYSTAL_AXE_2H_INACTIVE)),
    ECHO_AXE("Echo axe", "Lumber Up", ImmutableList.of(
            ItemID.LEAGUE_TRAILBLAZER_AXE)),
    DRAGON_HARPOON("Dragon harpoon", "Fishstabber", ImmutableList.of(
            ItemID.DRAGON_HARPOON,
            ItemID.TRAILBLAZER_HARPOON_NO_INFERNAL,
            ItemID.TRAILBLAZER_RELOADED_HARPOON_NO_INFERNAL)),
    INFERNAL_HARPOON("Infernal harpoon", "Fishstabber", ImmutableList.of(
            ItemID.INFERNAL_HARPOON,
            ItemID.INFERNAL_HARPOON_EMPTY,
            ItemID.TRAILBLAZER_HARPOON,
            ItemID.TRAILBLAZER_HARPOON_EMPTY,
            ItemID.TRAILBLAZER_RELOADED_HARPOON,
            ItemID.TRAILBLAZER_RELOADED_HARPOON_EMPTY)),
    CRYSTAL_HARPOON("Crystal harpoon", "Fishstabber", ImmutableList.of(
            ItemID.CRYSTAL_HARPOON,
            ItemID.CRYSTAL_HARPOON_INACTIVE)),
    ECHO_HARPOON("Echo harpoon", "Fishstabber", ImmutableList.of(
            ItemID.LEAGUE_TRAILBLAZER_HARPOON)),
    DRAGON_PICKAXE("Dragon pickaxe", "Rock Knocker", ImmutableList.of(
            ItemID.DRAGON_PICKAXE,
            ItemID.DRAGON_PICKAXE_PRETTY,
            ItemID.ZALCANO_PICKAXE,
            ItemID.TRAILBLAZER_PICKAXE_NO_INFERNAL,
            ItemID.TRAILBLAZER_RELOADED_PICKAXE_NO_INFERNAL)),
    INFERNAL_PICKAXE("Infernal pickaxe", "Rock Knocker", ImmutableList.of(
            ItemID.INFERNAL_PICKAXE,
            ItemID.INFERNAL_PICKAXE_EMPTY,
            ItemID.TRAILBLAZER_PICKAXE,
            ItemID.TRAILBLAZER_PICKAXE_EMPTY,
            ItemID.TRAILBLAZER_RELOADED_PICKAXE,
            ItemID.TRAILBLAZER_RELOADED_PICKAXE_EMPTY)),
    THIRD_AGE_PICKAXE("3rd Age pickaxe", "Rock Knocker", ImmutableList.of(
            ItemID._3A_PICKAXE)),
    CRYSTAL_PICKAXE("Crystal pickaxe", "Rock Knocker", ImmutableList.of(
            ItemID.CRYSTAL_PICKAXE,
            ItemID.CRYSTAL_PICKAXE_INACTIVE)),
    ECHO_PICKAXE("Echo pickaxe", "Rock Knocker", ImmutableList.of(
            ItemID.LEAGUE_TRAILBLAZER_PICKAXE)),
    ANCIENT_GODSWORD("Ancient godsword", "Blood Sacrifice", ImmutableList.of(
            ItemID.ANCIENT_GODSWORD,
            ItemID.BR_ANCIENT_GODSWORD)),
    ELDRITCH_NIGHTMARE_STAFF("Eldritch Nightmare staff", "Invocate", ImmutableList.of(
            ItemID.NIGHTMARE_STAFF_ELDRITCH)),
    KERIS_PARTISAN_OF_THE_SUN("Keris partisan of the sun", "Tumeken's Light", ImmutableList.of(
            ItemID.KERIS_PARTISAN_SUN)),
    PURGING_STAFF("Purging staff", "Scatter ashes", ImmutableList.of(
            ItemID.PURGING_STAFF,
            ItemID.BR_PURGING_STAFF)),
    TOXIC_BLOWPIPE("Toxic blowpipe", "Toxic Siphon", ImmutableList.of(
            ItemID.TOXIC_BLOWPIPE_LOADED)),
    SARADOMIN_GODSWORD("Saradomin godsword", "Healing Blade", ImmutableList.of(
            ItemID.SGS,
            ItemID.SGSG)),
    BRINE_SABRE("Brine sabre", "Liquify", ImmutableList.of(
            ItemID.OLAF2_BRINE_SABRE)),
    DRAGON_BATTLEAXE("Dragon battleaxe", "Rampage", ImmutableList.of(
            ItemID.DRAGON_BATTLEAXE,
            ItemID.BH_DRAGON_BATTLEAXE_CORRUPTED)),
    EXCALIBUR("Excalibur", "Sanctuary", ImmutableList.of(
            ItemID.EXCALIBUR)),
    DINHS_BULWARK("Dinh's bulwark", "Shield Bash", ImmutableList.of(
            ItemID.DINHS_BULWARK)),
    DRAGON_CROSSBOW("Dragon crossbow", "Annihilate", ImmutableList.of(
            ItemID.XBOWS_CROSSBOW_DRAGON,
            ItemID.BR_XBOWS_CROSSBOW_DRAGON)),
    DRAGON_HALBERD("Dragon halberd", "Sweep", ImmutableList.of(
            ItemID.DRAGON_HALBERD)),
    CRYSTAL_HALBERD("Crystal halberd", "Sweep", ImmutableList.of(
            ItemID.CRYSTAL_HALBERD,
            ItemID.CRYSTAL_HALBERD_2500)),
    DRAGON_2H_SWORD("Dragon 2h sword", "Powerstab", ImmutableList.of(
            ItemID.DRAGON_2H_SWORD,
            ItemID.BR_DRAGON_2H)),
    RUNE_THROWNAXE("Rune thrownaxe", "Chainhit", ImmutableList.of(
            ItemID.RUNE_THROWNAXE)),
    VESTAS_SPEAR("Vesta's spear", "Spear Wall", ImmutableList.of(
            ItemID.VESTAS_SPEAR)),
    ABYSSAL_WHIP("Abyssal whip", "Energy Drain", ImmutableList.of(
            ItemID.ABYSSAL_WHIP,
            ItemID.ABYSSAL_WHIP_LAVA,
            ItemID.ABYSSAL_WHIP_ICE,
            ItemID.BR_ABYSSAL_WHIP,
            ItemID.LEAGUE_3_WHIP)),
    ACCURSED_SCEPTRE("Accursed sceptre", "Condemn", ImmutableList.of(
            ItemID.WILD_CAVE_ACCURSED_CHARGED,
            ItemID.WILD_CAVE_ACCURSED_CHARGED_RECOL)),
    ANCIENT_MACE("Ancient mace", "Favour of the War God", ImmutableList.of(
            ItemID.ANCIENT_GOBLIN_MACE)),
    BANDOS_GODSWORD("Bandos godsword", "Warstrike", ImmutableList.of(
            ItemID.BGS,
            ItemID.BGSG)),
    BARRELCHEST_ANCHOR("Barrelchest anchor", "Sunder", ImmutableList.of(
            ItemID.BRAIN_ANCHOR)),
    BONE_DAGGER("Bone dagger", "Backstab", ImmutableList.of(
            ItemID.DTTD_BONE_DAGGER,
            ItemID.DTTD_BONE_DAGGER_P,
            ItemID.DTTD_BONE_DAGGER_P_,
            ItemID.DTTD_BONE_DAGGER_P__)),
    DARKLIGHT("Darklight", "Weaken", ImmutableList.of(
            ItemID.DARKLIGHT)),
    ARCLIGHT("Arclight", "Weaken", ImmutableList.of(
            ItemID.ARCLIGHT)),
    EMBERLIGHT("Emberlight", "Weaken", ImmutableList.of(
            ItemID.EMBERLIGHT)),
    DORGESHUUN_CROSSBOW("Dorgeshuun crossbow", "Snipe", ImmutableList.of(
            ItemID.DTTD_BONE_CROSSBOW)),
    DRAGON_SCIMITAR("Dragon scimitar", "Sever", ImmutableList.of(
            ItemID.DRAGON_SCIMITAR,
            ItemID.DRAGON_SCIMITAR_ORNAMENT,
            ItemID.BR_DRAGON_SCIMITAR)),
    DRAGON_WARHAMMER("Dragon warhammer", "Smash", ImmutableList.of(
            ItemID.DRAGON_WARHAMMER,
            ItemID.BR_DRAGON_WARHAMMER,
            ItemID.DRAGON_WARHAMMER_ORNAMENT)),
    STATIUSS_WARHAMMER("Statius's warhammer", "Smash", ImmutableList.of(
            ItemID.STATIUS_WARHAMMER,
            ItemID.BR_STATIUS_WARHAMMER)),
    ELDER_MAUL("Elder maul", "Pulverize", ImmutableList.of(
            ItemID.ELDER_MAUL,
            ItemID.BR_ELDER_MAUL,
            ItemID.ELDER_MAUL_ORNAMENT)),
    EYE_OF_AYAK("Eye of Ayak", "Soul Rend", ImmutableList.of(
            ItemID.EYE_OF_AYAK)),
    MORRIGANS_THROWING_AXE("Morrigan's throwing axe", "Hamstring", ImmutableList.of(
            ItemID.MORRIGANS_THROWNAXE)),
    SEERCULL("Seercull", "Soulshot", ImmutableList.of(
            ItemID.DAGANOTH_CAVE_MAGIC_SHORTBOW)),
    STAFF_OF_THE_DEAD("Staff of the Dead", "Power of Death", ImmutableList.of(
            ItemID.SOTD,
            ItemID.BR_SOTD,
            ItemID.TOXIC_SOTD_CHARGED,
            ItemID.TOXIC_SOTD_CHARGED_DEADMAN,
            ItemID.STAFF_OF_LIGHT,
            ItemID.STAFF_OF_BALANCE)),
    TONALZTICS_OF_RALOS("Tonalztics of Ralos", "Division", ImmutableList.of(
            ItemID.TONALZTICS_OF_RALOS_CHARGED)),
    ABYSSAL_BLUDGEON("Abyssal bludgeon", "Penance", ImmutableList.of(
            ItemID.ABYSSAL_BLUDGEON)),
    ARMADYL_CROSSBOW("Armadyl crossbow", "Armadyl Eye", ImmutableList.of(
            ItemID.ACB,
            ItemID.BR_ACB)),
    ARKAN_BLADE("Arkan blade", "Flames of Ralos", ImmutableList.of(
            ItemID.ARKAN_BLADE)),
    ARMADYL_GODSWORD("Armadyl godsword", "The Judgement", ImmutableList.of(
            ItemID.AGS,
            ItemID.AGSG,
            ItemID.BR_AGS,
            ItemID.DEADMAN_AGS)),
    BLUE_MOON_SPEAR("Blue Moon spear", "Break Shackles", ImmutableList.of(
            ItemID.FROSTMOON_SPEAR,
            ItemID.BR_FROSTMOON_SPEAR)),
    CRIMSON_KISTEN("Crimson kisten", "Brutal Swing", ImmutableList.of(
            ItemID.CRIMSON_KISTEN)),
    DAWNBRINGER("Dawnbringer", "Pulsate", ImmutableList.of(
            ItemID.VERZIK_SPECIAL_WEAPON)),
    DRAGON_HASTA("Dragon hasta", "Unleash", ImmutableList.of(
            ItemID.BRUT_DRAGON_SPEAR,
            ItemID.BRUT_DRAGON_SPEAR_P,
            ItemID.BRUT_DRAGON_SPEAR_P_,
            ItemID.BRUT_DRAGON_SPEAR_P__,
            ItemID.BRUT_DRAGON_SPEAR_KP)),
    DRAGON_LONGSWORD("Dragon longsword", "Cleave", ImmutableList.of(
            ItemID.DRAGON_LONGSWORD)),
    DRAGON_MACE("Dragon mace", "Shatter", ImmutableList.of(
            ItemID.DRAGON_MACE)),
    DRAGON_SWORD("Dragon sword", "Wild Stab", ImmutableList.of(
            ItemID.DRAGON_SHORTSWORD,
            ItemID.BR_DRAGON_SWORD)),
    DRAGON_THROWNAXE("Dragon thrownaxe", "Momentum Throw", ImmutableList.of(
            ItemID.DRAGON_THROWNAXE,
            ItemID.BR_DRAGON_THROWNAXE)),
    DUAL_MACUAHUITL("Dual macuahuitl", "Blood Infusion", ImmutableList.of(
            ItemID.DUAL_MACUAHUITL,
            ItemID.BR_DUAL_MACUAHUITL)),
    ECLIPSE_ATLATL("Eclipse atlatl", "Eclipse", ImmutableList.of(
            ItemID.ECLIPSE_ATLATL,
            ItemID.BR_ECLIPSE_ATLATL)),
    GRANITE_HAMMER("Granite hammer", "Hammer Blow", ImmutableList.of(
            ItemID.GRANITE_HAMMER)),
    KERIS_PARTISAN_OF_CORRUPTION("Keris partisan of corruption", "Wrath of Amascut", ImmutableList.of(
            ItemID.KERIS_PARTISAN_CORRUPTION)),
    LIGHT_BALLISTA("Light ballista", "Concentrated Shot", ImmutableList.of(
            ItemID.LIGHT_BALLISTA,
            ItemID.BR_LIGHT_BALLISTA)),
    HEAVY_BALLISTA("Heavy ballista", "Concentrated Shot", ImmutableList.of(
            ItemID.HEAVY_BALLISTA,
            ItemID.BR_HEAVY_BALLISTA,
            ItemID.HEAVY_BALLISTA_ORNAMENT)),
    MAGIC_LONGBOW("Magic longbow", "Powershot", ImmutableList.of(
            ItemID.MAGIC_LONGBOW)),
    MAGIC_COMP_BOW("Magic comp bow", "Powershot", ImmutableList.of(
            ItemID.TRAIL_COMPOSITE_BOW_MAGIC)),
    MORRIGANS_JAVELIN("Morrigan's javelin", "Phantom Strike", ImmutableList.of(
            ItemID.MORRIGANS_JAVELIN,
            ItemID.BR_MORRIGANS_JAVELIN)),
    NOXIOUS_HALBERD("Noxious halberd", "Virulence", ImmutableList.of(
            ItemID.NOXIOUS_HALBERD,
            ItemID.BR_NOXIOUS_HALBERD)),
    OSMUMTENS_FANG("Osmumten's fang", "Eviscerate", ImmutableList.of(
            ItemID.OSMUMTENS_FANG,
            ItemID.OSMUMTENS_FANG_ORNAMENT,
            ItemID.BR_OSMUMTENS_FANG)),
    RUNE_CLAWS("Rune claws", "Impale", ImmutableList.of(
            ItemID.RUNE_CLAWS)),
    SARADOMINS_BLESSED_SWORD("Saradomin's blessed sword", "Saradomin's Blessed Lightning", ImmutableList.of(
            ItemID.BLESSED_SARADOMIN_SWORD,
            ItemID.BLESSED_SARADOMIN_SWORD_DEGRADED)),
    SOULFLAME_HORN("Soulflame horn", "Entice", ImmutableList.of(
            ItemID.SOULFLAME_HORN)),
    SUNSPEAR("Sunspear", "Seeking Lunge", ImmutableList.of(
            ItemID.SUNSPEAR)),
    VESTAS_LONGSWORD("Vesta's longsword", "Feint", ImmutableList.of(
            ItemID.VESTAS_LONGSWORD,
            ItemID.BR_VESTAS_LONGSWORD,
            ItemID.BH_VESTAS_LONGSWORD)),
    VOIDWAKER("Voidwaker", "Disrupt", ImmutableList.of(
            ItemID.VOIDWAKER,
            ItemID.BR_VOIDWAKER)),
    VOLATILE_NIGHTMARE_STAFF("Volatile Nightmare staff", "Immolate", ImmutableList.of(
            ItemID.NIGHTMARE_STAFF_VOLATILE,
            ItemID.BR_NIGHTMARE_STAFF_VOLATILE)),
    ZARYTE_CROSSBOW("Zaryte crossbow", "Evoke", ImmutableList.of(
            ItemID.ZARYTE_XBOW,
            ItemID.BR_ZARYTE_XBOW)),
    ABYSSAL_DAGGER("Abyssal dagger", "Abyssal Puncture", ImmutableList.of(
            ItemID.ABYSSAL_DAGGER,
            ItemID.ABYSSAL_DAGGER_P,
            ItemID.ABYSSAL_DAGGER_P_,
            ItemID.ABYSSAL_DAGGER_P__,
            ItemID.BH_ABYSSAL_DAGGER_IMBUE,
            ItemID.BH_ABYSSAL_DAGGER_P_IMBUE,
            ItemID.BH_ABYSSAL_DAGGER_P__IMBUE,
            ItemID.BH_ABYSSAL_DAGGER_P___IMBUE)),
    BURNING_CLAWS("Burning claws", "Burning barrage", ImmutableList.of(
            ItemID.BONE_CLAWS,
            ItemID.BR_BONE_CLAWS)),
    DARK_BOW("Dark bow", "Descent of Darkness", ImmutableList.of(
            ItemID.DARKBOW,
            ItemID.DARKBOW_GREEN,
            ItemID.DARKBOW_BLUE,
            ItemID.DARKBOW_YELLOW,
            ItemID.DARKBOW_WHITE,
            ItemID.BR_DARKBOW)),
    DRAGON_CLAWS("Dragon claws", "Slice and Dice", ImmutableList.of(
            ItemID.DRAGON_CLAWS,
            ItemID.BR_DRAGON_CLAWS,
            ItemID.DRAGON_CLAWS_ORNAMENT,
            ItemID.BH_DRAGON_CLAWS_CORRUPTED)),
    DRAGON_DAGGER("Dragon dagger", "Puncture", ImmutableList.of(
            ItemID.DRAGON_DAGGER,
            ItemID.DRAGON_DAGGER_P,
            ItemID.DRAGON_DAGGER_P_,
            ItemID.DRAGON_DAGGER_P__,
            ItemID.BR_DRAGON_DAGGER,
            ItemID.BH_DRAGON_DAGGER_CORRUPTED,
            ItemID.BH_DRAGON_DAGGER_P_CORRUPTED,
            ItemID.BH_DRAGON_DAGGER_P__CORRUPTED,
            ItemID.BH_DRAGON_DAGGER_P___CORRUPTED)),
    DRAGON_KNIFE("Dragon knife", "Duality", ImmutableList.of(
            ItemID.DRAGON_KNIFE,
            ItemID.DRAGON_KNIFE_P,
            ItemID.DRAGON_KNIFE_P_,
            ItemID.DRAGON_KNIFE_P__,
            ItemID.BR_DRAGON_KNIFE)),
    GRANITE_MAUL("Granite maul", "Quick Smash", ImmutableList.of(
            ItemID.GRANITE_MAUL,
            ItemID.GRANITE_MAUL_PRETTY,
            ItemID.BR_GRANITE_MAUL,
            ItemID.GRANITE_MAUL_PLUS,
            ItemID.GRANITE_MAUL_PRETTY_PLUS)),
    ROSEWOOD_BLOWPIPE("Rosewood blowpipe", "Rapid Burst", ImmutableList.of(
            ItemID.ROSEWOOD_BLOWPIPE)),
    MAGIC_SHORTBOW("Magic shortbow", "Snapshot", ImmutableList.of(
            ItemID.MAGIC_SHORTBOW,
            ItemID.MAGIC_SHORTBOW_I,
            ItemID.BR_MAGIC_BOW)),
    SARADOMIN_SWORD("Saradomin sword", "Saradomin's Lightning", ImmutableList.of(
            ItemID.SARADOMIN_SWORD)),
    VESTAS_SPEAR_BOUNTY_HUNTER("Vesta's spear (Bounty Hunter)", "Spear Wall", ImmutableList.of(
            ItemID.VESTAS_SPEAR_BH)),
    WEBWEAVER_BOW("Webweaver bow", "Swarm", ImmutableList.of(
            ItemID.WILD_CAVE_WEBWEAVER_CHARGED)),
    ABYSSAL_TENTACLE("Abyssal tentacle", "Binding Tentacle", ImmutableList.of(
            ItemID.ABYSSAL_TENTACLE,
            ItemID.LEAGUE_3_WHIP_TENTACLE)),
    DRAGON_SPEAR("Dragon spear", "Shove", ImmutableList.of(
            ItemID.DRAGON_SPEAR,
            ItemID.DRAGON_SPEAR_P,
            ItemID.DRAGON_SPEAR_P_,
            ItemID.DRAGON_SPEAR_P__,
            ItemID.TBWT_DRAGON_SPEAR_KP,
            ItemID.BH_DRAGON_SPEAR_CORRUPTED,
            ItemID.BH_DRAGON_SPEAR_P_CORRUPTED,
            ItemID.BH_DRAGON_SPEAR_P__CORRUPTED,
            ItemID.BH_DRAGON_SPEAR_P___CORRUPTED)),
    ZAMORAKIAN_SPEAR("Zamorakian spear", "Shove", ImmutableList.of(
            ItemID.ZAMORAK_SPEAR)),
    ZAMORAKIAN_HASTA("Zamorakian hasta", "Shove", ImmutableList.of(
            ItemID.ZAMORAK_HASTA)),
    ROD_OF_IVANDIS("Rod of Ivandis", "Retainer", ImmutableList.of(
            ItemID.BURGH_ROD_COMMAND_FINAL_10,
            ItemID.BURGH_ROD_COMMAND_FINAL_9,
            ItemID.BURGH_ROD_COMMAND_FINAL_8,
            ItemID.BURGH_ROD_COMMAND_FINAL_7,
            ItemID.BURGH_ROD_COMMAND_FINAL_6,
            ItemID.BURGH_ROD_COMMAND_FINAL_5,
            ItemID.BURGH_ROD_COMMAND_FINAL_4,
            ItemID.BURGH_ROD_COMMAND_FINAL_3,
            ItemID.BURGH_ROD_COMMAND_FINAL_2,
            ItemID.BURGH_ROD_COMMAND_FINAL_1)),
    IVANDIS_FLAIL("Ivandis flail", "Retainer", ImmutableList.of(
            ItemID.IVANDIS_FLAIL)),
    BLISTERWOOD_FLAIL("Blisterwood flail", "Retainer", ImmutableList.of(
            ItemID.BLISTERWOOD_FLAIL)),
    HALLOWED_FLAIL("Hallowed flail", "Retainer", ImmutableList.of(
            ItemID.HALLOWED_FLAIL)),
    SCORCHING_BOW("Scorching bow", "Scorching shackles", ImmutableList.of(
            ItemID.SCORCHING_BOW)),
    URSINE_CHAINMACE("Ursine chainmace", "Bear Down", ImmutableList.of(
            ItemID.WILD_CAVE_URSINE_CHARGED)),
    ZAMORAK_GODSWORD("Zamorak godsword", "Ice Cleave", ImmutableList.of(
            ItemID.ZGS,
            ItemID.ZGSG)),
    ANCIENT_WYVERN_SHIELD("Ancient wyvern shield", "None", ImmutableList.of(
            ItemID.WYVERN_SHIELD)),
    DRAGONFIRE_SHIELD("Dragonfire shield", "None", ImmutableList.of(
            ItemID.DRAGONFIRE_SHIELD,
            ItemID.BR_DRAGONFIRE_SHIELD)),
    DRAGONFIRE_WARD("Dragonfire ward", "None", ImmutableList.of(
            ItemID.DRAGONFIRE_WARD)),
    SOULREAPER_AXE("Soulreaper axe", "Behead", ImmutableList.of(
            ItemID.SOULREAPER,
            ItemID.SOULREAPER_AXE_ORN)),
    CRYSTAL_DAGGER_PERFECTED("Crystal dagger (perfected)", "Crystalline Severance", ImmutableList.of(
            ItemID.ECHO_GAUNTLET_CRYSTAL_DAGGER_T3)),
    THE_DOGSWORD("The dogsword", "Echo slash", ImmutableList.of(
            ItemID.ECHO_GODSWORD,
            ItemID.DEADMAN_DOGSWORD)),
    FANG_OF_THE_HOUND("Fang of the hound", "Ferocity", ImmutableList.of(
            ItemID.FANG_OF_THE_HOUND)),
    INFERNAL_TECPATL("Infernal tecpatl", "Brand", ImmutableList.of(
            ItemID.INFERNAL_TECPATL)),
    LITHIC_SCEPTRE("Lithic sceptre", "Explosive Shatter", ImmutableList.of(
            ItemID.LITHIC_SCEPTRE_CHARGED)),
    SUNLIGHT_SPEAR("Sunlight spear", "Sol Slam", ImmutableList.of(
            ItemID.WEAPON_OF_SOL)),
    THUNDER_KHOPESH("Thunder khopesh", "Lingering Lightning", ImmutableList.of(
            ItemID.THUNDER_KHOPESH,
            ItemID.DEADMAN_THUNDER_KHOPESH));


    private static final ImmutableMap<String, SpecWeapon> WEAPONS_BY_NAME = Arrays.stream(values())
            .collect(toImmutableMap(spec -> spec.getName().toLowerCase(Locale.ROOT), Function.identity()));
    private static final ImmutableMap<Integer, SpecWeapon> WEAPONS_BY_ID = Arrays.stream(values())
            .flatMap(weapon -> weapon.weaponIds.stream().map(id -> Map.entry(id, weapon)))
            .collect(toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));
    private final String name;
    private final String specialAttackName;
    private final ImmutableList<Integer> weaponIds;

    public static SpecWeapon fromId(int id) {
        return WEAPONS_BY_ID.getOrDefault(id, UNKNOWN);
    }

    public static SpecWeapon fromString(String str) {
        SpecWeapon byName = WEAPONS_BY_NAME.getOrDefault(str.toLowerCase(Locale.ROOT), UNKNOWN);
        if (!byName.isUnknown()) return byName;

        try {
            return fromId(Integer.parseInt(str));
        } catch (NumberFormatException e) {
            return UNKNOWN;
        }
    }

    public TextEntry nameAsTextEntry() {
        return new TextEntry(name, Optional.empty());
    }

    public TextEntry specialAttackNameAsTextEntry() {
        return new TextEntry(specialAttackName, Optional.empty());
    }


    public boolean isUnknown() {
        return this == UNKNOWN;
    }
}