package com.aguscerdo.animechant.chantdata;

import com.aguscerdo.animechant.chantdata.model.ChantData;
import com.aguscerdo.animechant.specials.model.SpecWeaponTextPreset;
import com.aguscerdo.animechant.spells.model.SpellTextPreset;
import com.google.inject.ImplementedBy;

import java.util.Optional;

/**
 * Loads Spell and Spec presets from a yaml file.
 */
@ImplementedBy(PresetLoaderImpl.class)
public interface PresetLoader {
    /**
     * Loads the raw data for the given preset.
     *
     * @return the parsed preset, or empty if the preset has no backing file,
     * the resource is missing, or the file is invalid or an unsupported version.
     */
    Optional<ChantData> loadPreset(SpellTextPreset preset);

    /**
     * Loads the raw data for the given preset.
     *
     * @return the parsed preset, or empty if the preset has no backing file,
     * the resource is missing, or the file is invalid or an unsupported version.
     */
    Optional<ChantData> loadPreset(SpecWeaponTextPreset preset);
}
