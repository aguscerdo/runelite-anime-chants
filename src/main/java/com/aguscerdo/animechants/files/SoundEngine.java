package com.aguscerdo.animechants.files;

import com.aguscerdo.animechants.AnimeChantsConfig;
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
    private AnimeChantsConfig config;

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


        float ratio = config.soundVolume() * 0.01f;
        float gain = 20f * (float) Math.log10(ratio * ratio);

        try {
            audioPlayer.play(resolved.get(), gain);
        } catch (Exception e) {
            log.warn("Failed to play audio file: {}", resolved, e);
        }
    }
}