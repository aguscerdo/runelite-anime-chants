package com.aguscerdo.animechant.presets;

import com.aguscerdo.animechant.TextPreset;

import java.util.Optional;

public interface PresetLoader {
    /**
     * Loads the raw data for the given preset.
     *
     * @return the parsed preset, or empty if the preset has no backing file,
     * the resource is missing, or the file is invalid or an unsupported version.
     */
    Optional<RawPresetData> loadPreset(TextPreset preset);
}
