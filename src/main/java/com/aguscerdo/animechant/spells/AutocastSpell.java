package com.aguscerdo.animechant.spells;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

@RequiredArgsConstructor
@Getter
public enum AutocastSpell {
    /**
     * The currently selected autocast spell.
     * 0        = no spell selected.
     * <p>
     * 1 -> 4   = wind, water, earth, fire strike
     * 5 -> 8   = wind, water, earth, fire bolt
     * 9 -> 12  = wind, water, earth, fire blast
     * 13 -> 16 = wind, water, earth, fire wave
     * 48 -> 51 = wind, water, earth, fire surge
     * <p>
     * 31 -> 34 = Smoke, Shadow, Blood, Ice Rush
     * 35 -> 38 = Smoke, Shadow, Blood, Ice Burst
     * 39 -> 42 = Smoke, Shadow, Blood, Ice Blitz
     * 43 -> 46 = Smoke, Shadow, Blood, Ice Barrage
     * <p>
     * 53 -> 55 = Inferior, Superior, Dark Demonbane
     * 56 -> 58 = Ghostly, Skeletal, Undead Grasp
     * <p>
     * 47       = Iban's blast
     * 17       = Crumble Undead
     * 18       = Magic Dart
     * 19       = Claws of Guthix
     * 20       = Flames of Zamorak
     * 52       = Saradomin Strike
     */

    NO_SPELL(Spell.UNKNOWN, 0),

    WIND_STRIKE(Spell.WIND_STRIKE, 1),
    WATER_STRIKE(Spell.WATER_STRIKE, 2),
    EARTH_STRIKE(Spell.EARTH_STRIKE, 3),
    FIRE_STRIKE(Spell.FIRE_STRIKE, 4),

    WIND_BOLT(Spell.WIND_BOLT, 5),
    WATER_BOLT(Spell.WATER_BOLT, 6),
    EARTH_BOLT(Spell.EARTH_BOLT, 7),
    FIRE_BOLT(Spell.FIRE_BOLT, 8),

    WIND_BLAST(Spell.WIND_BLAST, 9),
    WATER_BLAST(Spell.WATER_BLAST, 10),
    EARTH_BLAST(Spell.EARTH_BLAST, 11),
    FIRE_BLAST(Spell.FIRE_BLAST, 12),

    WIND_WAVE(Spell.WIND_WAVE, 13),
    WATER_WAVE(Spell.WATER_WAVE, 14),
    EARTH_WAVE(Spell.EARTH_WAVE, 15),
    FIRE_WAVE(Spell.FIRE_WAVE, 16),

    CRUMBLE_UNDEAD(Spell.CRUMBLE_UNDEAD, 17),
    MAGIC_DART(Spell.MAGIC_DART, 18),

    CLAWS_OF_GUTHIX(Spell.CLAWS_OF_GUTHIX, 19),
    FLAMES_OF_ZAMORAK(Spell.FLAMES_OF_ZAMORAK, 20),

    // 21 - 30 Unused

    SMOKE_RUSH(Spell.SMOKE_RUSH, 31),
    SHADOW_RUSH(Spell.SHADOW_RUSH, 32),
    BLOOD_RUSH(Spell.BLOOD_RUSH, 33),
    ICE_RUSH(Spell.ICE_RUSH, 34),

    SMOKE_BURST(Spell.SMOKE_BURST, 35),
    SHADOW_BURST(Spell.SHADOW_BURST, 36),
    BLOOD_BURST(Spell.BLOOD_BURST, 37),
    ICE_BURST(Spell.ICE_BURST, 38),

    SMOKE_BLITZ(Spell.SMOKE_BLITZ, 39),
    SHADOW_BLITZ(Spell.SHADOW_BLITZ, 40),
    BLOOD_BLITZ(Spell.BLOOD_BLITZ, 41),
    ICE_BLITZ(Spell.ICE_BLITZ, 42),

    SMOKE_BARRAGE(Spell.SMOKE_BARRAGE, 43),
    SHADOW_BARRAGE(Spell.SHADOW_BARRAGE, 44),
    BLOOD_BARRAGE(Spell.BLOOD_BARRAGE, 45),
    ICE_BARRAGE(Spell.ICE_BARRAGE, 46),

    IBAN_BLAST(Spell.IBAN_BLAST, 47),

    WIND_SURGE(Spell.WIND_SURGE, 48),
    WATER_SURGE(Spell.WATER_SURGE, 49),
    EARTH_SURGE(Spell.EARTH_SURGE, 50),
    FIRE_SURGE(Spell.FIRE_SURGE, 51),

    SARADOMIN_STRIKE(Spell.SARADOMIN_STRIKE, 52),

    INFERIOR_DEMONBANE(Spell.INFERIOR_DEMONBANE, 53),
    SUPERIOR_DEMONBANE(Spell.SUPERIOR_DEMONBANE, 54),
    DARK_DEMONBANE(Spell.DARK_DEMONBANE, 55),

    GHOSTLY_GRASP(Spell.GHOSTLY_GRASP, 56),
    SKELETAL_GRASP(Spell.SKELETAL_GRASP, 57),
    UNDEAD_GRASP(Spell.UNDEAD_GRASP, 58);

    private final Spell spell;
    private final int varbitValue;

    private static final Map<Integer, Spell> VARBIT_TO_SPELL;

    static {
        VARBIT_TO_SPELL = Arrays.stream(values()).collect(
                toImmutableMap(
                        AutocastSpell::getVarbitValue,
                        AutocastSpell::getSpell
                )
        );

    }

    public static Spell spellFromAutocast(int varbitValue) {
        return VARBIT_TO_SPELL.getOrDefault(varbitValue, Spell.UNKNOWN);
    }
}