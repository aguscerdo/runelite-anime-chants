package com.aguscerdo.animechant.chantdata;

import com.aguscerdo.animechant.chantdata.model.ChantData;
import com.aguscerdo.animechant.files.FileManager;
import com.aguscerdo.animechant.files.PluginDirectory;
import com.aguscerdo.animechant.specials.model.SpecWeaponTextPreset;
import com.aguscerdo.animechant.spells.model.SpellTextPreset;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.util.Filepath;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

@Slf4j
@Singleton
public class PresetLoaderImpl implements PresetLoader {
    private static final ObjectMapper YAML_MAPPER = PresetYaml.createMapper();

    @Inject
    private PluginDirectory pluginDirectoryGetter;

    /**
     * Reads the tree, checks "version", binds with that version's schema. Closes the reader.
     */
    private static Optional<ChantData> loadFromYaml(Reader reader, String source) {
        try (Reader r = reader) {
            JsonNode root = YAML_MAPPER.readTree(r);
            if (root == null || root.isMissingNode() || !root.isObject()) {
                log.warn("Preset {} is empty or not a mapping", source);
                return Optional.empty();
            }

            // A missing "version" key is treated as the current version.
            int version = root.path("version").asInt(ChantData.CURRENT_VERSION);

            if (version == 1) {
                return Optional.of(YAML_MAPPER.treeToValue(root, ChantData.class));
            }
            log.error("Unsupported yaml schema version: {} in {}", version, source);
            return Optional.empty();
        } catch (IOException e) {
            log.warn("Invalid YAML in {}", source, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ChantData> loadPreset(SpellTextPreset preset) {
        return loadPresetFile(preset.getDirectoryName(), preset.getFileName());
    }

    @Override
    public Optional<ChantData> loadPreset(SpecWeaponTextPreset preset) {
        return loadPresetFile(preset.getDirectoryName(), preset.getFileName());
    }

    @SuppressWarnings("OptionalIsPresent")
    private Optional<ChantData> loadPresetFile(String directoryName, String fileName) {
        Optional<Filepath> filepath = pluginDirectoryGetter.getPluginDirectory();
        if (filepath.isEmpty()) {
            return Optional.empty();
        }

        return FileManager.getPresetFileReader(filepath.get(), directoryName, fileName)
                .flatMap(reader -> loadFromYaml(reader, fileName));
    }
}