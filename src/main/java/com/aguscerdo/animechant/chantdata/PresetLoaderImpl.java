package com.aguscerdo.animechant.chantdata;

import com.aguscerdo.animechant.TextPreset;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@Singleton
public class PresetLoaderImpl implements PresetLoader {
    // Resource paths always use '/', regardless of OS (so no java.nio.file.Path here).
    private static final String PRESETS_DIR = "/com/aguscerdo/animechant/chantdata";

    private static final ObjectMapper YAML_MAPPER = PresetYaml.createMapper();

    /**
     * Reads the file into a tree, checks "version", then binds with that version's
     * schema. Every version ends up as the same canonical RawPresetData.
     */
    private static Optional<ChantData> loadFromYaml(Reader reader, String source) {
        try {
            JsonNode root = YAML_MAPPER.readTree(reader);
            if (root == null || root.isMissingNode() || !root.isObject()) {
                log.warn("Preset {} is empty or not a mapping", source);
                return Optional.empty();
            }

            // A missing "version" key is treated as the current version.
            int version = root.path("version").asInt(ChantData.CURRENT_VERSION);

            if (version == 1) {
                return Optional.of(YAML_MAPPER.treeToValue(root, ChantData.class));
            } else {
                log.error("Unsupported yaml schema version: {}\n{}", version, source);
                return Optional.empty();
            }

        } catch (IOException e) {
            log.warn("Invalid YAML in {}", source, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ChantData> loadPreset(TextPreset preset) {
        // Spell name doesn't need a mapping, use spell names directly.
        if (preset == TextPreset.SPELL_NAME) {
            return Optional.empty();
        }

        if (preset.getFileName().isEmpty()) {
            return Optional.empty();
        }

        String resourcePath = PRESETS_DIR + "/" + preset.getFileName();
        try (InputStream in = PresetLoaderImpl.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                log.warn("Preset resource {} not found", resourcePath);
                return Optional.empty();
            }

            return loadFromYaml(new InputStreamReader(in, StandardCharsets.UTF_8), resourcePath);
        } catch (IOException e) {
            log.warn("Failed reading preset {}", resourcePath, e);
            return Optional.empty();
        }
    }
}
