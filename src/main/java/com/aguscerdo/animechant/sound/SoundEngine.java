package com.aguscerdo.animechant.sound;

import com.aguscerdo.animechant.AnimeChantConfig;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.audio.AudioPlayer;
import net.runelite.client.util.Filepath;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

@Singleton
@Slf4j
public class SoundEngine {

    @Inject
    private AnimeChantConfig config;

    @Inject
    private AudioPlayer audioPlayer;

    @Inject
    private ScheduledExecutorService executor; // RuneLite's shared pool


    public void playSound(Filepath pluginDir, String soundFile) {
        if (config.soundVolume() == 0) {
            return;
        }

        Optional<Filepath> resolved = FileManager.resolvePath(pluginDir, soundFile);
        if (resolved.isEmpty()) return;


        float gain = 20f * (float) Math.log10(config.soundVolume() / 100f);

        try {
            audioPlayer.play(resolved.get(), gain);
        } catch (Exception e) {
            log.warn("Failed to play audio file: {}", resolved, e);
        }
    }
}