package com.aguscerdo.animechant.files;

import com.aguscerdo.animechant.AnimeChantConfig;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.audio.AudioPlayer;
import net.runelite.client.util.Filepath;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
@Slf4j
public class SoundEngine {

    @Inject
    private AnimeChantConfig config;

    @Inject
    private AudioPlayer audioPlayer;

    public void playSound(Filepath pluginDir, String soundFile) {
        if (config.soundVolume() == 0) {
            return;
        }

        Optional<Filepath> resolved = FileManager.resolveSoundFile(pluginDir, soundFile);
        if (resolved.isEmpty()) {
            log.debug("Could not resolve files file: {}", soundFile);
            return;
        }

        log.debug("Playing files at {}", resolved);


        float gain = 20f * (float) Math.log10(config.soundVolume() / 100f);

        try {
            audioPlayer.play(resolved.get(), gain);
        } catch (Exception e) {
            log.warn("Failed to play audio file: {}", resolved, e);
        }
    }
}