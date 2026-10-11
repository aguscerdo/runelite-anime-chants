package com.aguscerdo.animechant.sound;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.util.Filepath;

import java.util.Optional;

@Slf4j
public class FileManager {
    public static Optional<Filepath> resolvePath(Filepath pluginDirectory, String filePath) {
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
}
