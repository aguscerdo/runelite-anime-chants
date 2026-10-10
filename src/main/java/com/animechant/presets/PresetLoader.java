package com.animechant.presets;

import com.animechant.TextPreset;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;

@Slf4j
@Singleton
public class PresetLoader {
    // Resource paths always use '/', regardless of OS (so no java.nio.file.Path here).
    private static final String PRESETS_DIR = "/presets";

    private static final ObjectMapper YAML_MAPPER = PresetYaml.createMapper();

    public Optional<RawPresetData> loadPreset(TextPreset preset) {
        // Spell name doesn't need a mapping, use spell names directly.
        if (preset == TextPreset.SPELL_NAME) {
            return Optional.empty();
        }

        if (preset.getFileName().isEmpty()) {
            return Optional.empty();
        }

        Path resourcePath = Path.of(PRESETS_DIR, preset.getFileName());
        try (InputStream in = PresetLoader.class.getResourceAsStream(resourcePath.toString())) {
            if (in == null) {
                log.warn("Preset resource {} not found", resourcePath);
                return Optional.empty();
            }

            return loadFromYaml(new InputStreamReader(in, StandardCharsets.UTF_8), resourcePath.toString());
        } catch (IOException e) {
            log.warn("Failed reading preset {}", resourcePath, e);
            return Optional.empty();
        }
    }

    /**
     * Reads the file into a tree, checks "version", then binds with that version's
     * schema. Every version ends up as the same canonical RawPresetData.
     */
    private static Optional<RawPresetData> loadFromYaml(Reader reader, String source) {
        try {
            JsonNode root = YAML_MAPPER.readTree(reader);
            if (root == null || root.isMissingNode() || !root.isObject()) {
                log.warn("Preset {} is empty or not a mapping", source);
                return Optional.empty();
            }

            // A missing "version" key is treated as the current version.
            int version = root.path("version").asInt(RawPresetData.CURRENT_VERSION);

            if (version == 1) {
                return Optional.of(YAML_MAPPER.treeToValue(root, RawPresetData.class));
            } else {
                log.error("Unsupported yaml schema version: {}\n{}", version, source);
                return Optional.empty();
            }

        } catch (IOException e) {
            log.warn("Invalid YAML in {}", source, e);
            return Optional.empty();
        }
    }
}