package com.aguscerdo.animechant;

import com.aguscerdo.animechant.chantdata.CustomOverrides;
import com.aguscerdo.animechant.chantdata.model.TextEntry;
import com.aguscerdo.animechant.files.PluginDirectory;
import com.aguscerdo.animechant.files.SoundEngine;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.util.Filepath;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@Singleton
public class TextAndSoundServiceImpl implements TextAndSoundService {

    @Inject
    private Client client;

    @Inject
    private ScheduledExecutorService executor;

    @Inject
    private AnimeChantConfig config;

    @Inject
    private SoundEngine soundEngine;

    @Inject
    private PluginDirectory pluginDirectoryGetter;


    private String ourText = "";
    private int ticksLeft;


    /**
     * Call once per GameTick. Clears the text exactly once, when the timer runs out.
     */
    public void onGameTick() {
        if (ticksLeft > 0 && --ticksLeft == 0) {
            clear();
        }
    }

    /**
     * Client thread only.
     */
    public void clear() {
        ticksLeft = 0;
        Player me = client.getLocalPlayer();
        if (me != null && !ourText.isEmpty() && ourText.equals(me.getOverheadText())) {
            me.setOverheadText(null);
        }
        ourText = "";
    }

    public void processTextEntry(TextEntry textEntry, boolean shoutText, boolean soundsEnabled) {
        displayText(textEntry, shoutText);

        if (soundsEnabled) {
            playSound(textEntry);
        }
    }

    public void processTextEntryOpt(Optional<TextEntry> textEntry, boolean shoutText, boolean soundsEnabled) {
        if (textEntry.isEmpty()) return;
        processTextEntry(textEntry.get(), shoutText, soundsEnabled);
    }


    public void displayText(TextEntry entry, boolean shout) {
        if (entry == null) return;

        String text = entry.getText();
        if (text == null || text.isBlank() || text.equals(CustomOverrides.NONE)) return;

        Player me = client.getLocalPlayer();
        if (me == null) return;

        if (shout && !(text.endsWith(".") || text.endsWith("!"))) {
            text += "!";
        }

        me.setOverheadText(text);
        ourText = text;
        ticksLeft = config.durationTicks();
        log.debug("Showing text: {}", entry);
    }


    /**
     * Plays sound
     */
    public void playSound(TextEntry textEntry) {
        Optional<String> soundFile = textEntry.getSound();
        if (soundFile.isEmpty() || !soundFile.get().endsWith(".wav")) return;

        Optional<Filepath> pluginDirectory = pluginDirectoryGetter.getPluginDirectory();
        if (pluginDirectory.isEmpty()) return;

        executor.execute(() -> {
            try {
                soundEngine.playSound(pluginDirectory.get(), soundFile.get());
            } catch (RuntimeException e) {
                log.error("Failed to play files file: {}", soundFile.get(), e);
            }
        });
    }
}