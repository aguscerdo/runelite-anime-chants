package com.aguscerdo.animechants.chantdata;

import com.aguscerdo.animechants.chantdata.model.ChantData;
import com.aguscerdo.animechants.chantdata.model.TextEntry;
import com.aguscerdo.animechants.files.FileManager;
import com.aguscerdo.animechants.files.PluginDirectory;
import com.aguscerdo.animechants.specials.model.SpecWeaponTextPreset;
import com.aguscerdo.animechants.spells.model.SpellTextPreset;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.util.Filepath;

import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

@Slf4j
@Singleton
public class PresetLoaderImpl implements PresetLoader {
    @Inject
    private PluginDirectory pluginDirectoryGetter;

    @Inject
    private Gson baseGson;

    private Gson gson;

    private Gson gson() {
        if (gson == null) {
            gson = baseGson.newBuilder()
                    .registerTypeAdapter(TextEntry.class, new TextEntry.Deserializer())
                    .registerTypeAdapter(ChantData.class, new ChantData.Deserializer())
                    .create();
        }
        return gson;
    }

    /**
     * Reads the tree, checks "version", binds with that version's schema. Closes the reader.
     */
    private Optional<ChantData> loadFromJson(Reader reader, String source) {
        try (Reader r = reader) {
            JsonElement root = gson().fromJson(r, JsonElement.class);
            if (root == null || !root.isJsonObject()) {
                log.warn("Preset {} is empty or not an object", source);
                return Optional.empty();
            }

            // A missing "version" key is treated as the current version.
            JsonElement v = root.getAsJsonObject().get("version");
            int version = v != null && v.isJsonPrimitive() ? v.getAsInt() : ChantData.CURRENT_VERSION;

            if (version == 1) {
                return Optional.of(gson().fromJson(root, ChantData.class));
            }
            log.error("Unsupported schema version: {} in {}", version, source);
            return Optional.empty();
        } catch (IOException | JsonParseException e) {
            log.warn("Invalid JSON in {}", source, e);
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
                .flatMap(reader -> loadFromJson(reader, fileName));
    }
}