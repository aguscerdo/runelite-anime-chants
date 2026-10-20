package com.aguscerdo.animechants.files;

import com.aguscerdo.animechants.chantdata.PresetLoaderImpl;
import com.aguscerdo.animechants.specials.model.SpecWeaponTextPreset;
import com.aguscerdo.animechants.spells.model.SpellTextPreset;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.util.Filepath;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Optional;

import static com.aguscerdo.animechants.tools.StringUtils.joinPath;

@Slf4j
public class FileManager {
    private static final String PRESETS_DIR = "/com/aguscerdo/animechants/chantdata";

    /**
     * Resolves a Sound file in the plugin directory if it exists
     */
    public static Optional<Filepath> resolveSoundFile(Filepath pluginDirectory, String filePath) {
        if (!filePath.endsWith(".wav")) {
            log.warn("File is not a `.wav`: {}", filePath);
            return Optional.empty();
        }

        if (filePath.startsWith(pluginDirectory.toString())) {
            filePath = filePath.substring(pluginDirectory.toString().length());
        }
        Filepath fullPath = pluginDirectory.join(filePath);

        if (!fullPath.exists()) {
            log.warn("File doesn't exist: {}", fullPath);
            return Optional.empty();
        }
        if (!fullPath.isFile()) {
            log.warn("Filepath is not a file: {}", fullPath);
            return Optional.empty();
        }

        return Optional.of(fullPath);
    }


    /**
     * Write preset files into the plugin data for manual edits
     */
    public static void copyPresetFiles(Filepath pluginDirectory, boolean force) {
        for (SpellTextPreset preset : SpellTextPreset.values()) {
            setUpPresetFiles(pluginDirectory, preset.getDirectoryName(), preset.getFileName(), force);
        }

        for (SpecWeaponTextPreset preset : SpecWeaponTextPreset.values()) {
            setUpPresetFiles(pluginDirectory, preset.getDirectoryName(), preset.getFileName(), force);
        }

    }

    /**
     * Get a file reader for a preset file.
     */
    public static Optional<Reader> getPresetFileReader(Filepath pluginDirectory, String directoryName, String fileName) {
        try {
            if (directoryName == null || directoryName.isEmpty() || fileName == null || fileName.isEmpty()) {
                return Optional.empty();
            }

            Filepath filepath = pluginDirectory.join(directoryName, fileName);
            return Optional.ofNullable(filepath.openReader());
        } catch (IOException e) {
            log.warn("Failed to read yaml preset: {}", fileName);
            return Optional.empty();
        }
    }

    private static void setUpPresetFiles(Filepath pluginDirectory, String directory, String fileName, boolean force) {
        try {
            if (directory.isEmpty() || fileName.isEmpty()) return;
            log.info("Setting up directories for {}", directory);

            Filepath folderPath = pluginDirectory.join(directory);
            folderPath.createDirectories();

            folderPath.join("spells").createDirectories();
            folderPath.join("specs").createDirectories();

            Filepath destinationPath = folderPath.join(fileName);
            if (destinationPath.exists() && !force) return;

            String origin = joinPath(PRESETS_DIR, fileName);

            try (InputStream in = PresetLoaderImpl.class.getResourceAsStream(origin)) {
                if (in == null) {
                    log.warn("Failed to read Java resource: {}", origin);
                    return;
                }

                destinationPath.write(in.readAllBytes());
                log.info("Successfully copied yaml preset to plugin data: {}", destinationPath);
            }
        } catch (IOException e) {
            log.warn("Failed to copy yaml preset from resource to plugin: {}", fileName, e);
        }
    }
}