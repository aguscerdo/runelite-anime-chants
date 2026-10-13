package com.aguscerdo.animechant;

import com.aguscerdo.animechant.chantdata.model.TextEntry;
import com.aguscerdo.animechant.sound.SoundEngine;
import com.aguscerdo.animechant.specials.SpecialAttackPlugin;
import com.aguscerdo.animechant.spells.SpellPlugin;
import com.google.inject.Binder;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Filepath;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@PluginDescriptor(
        name = "Anime Chant",
        description = "Shows custom text when you cast spells",
        tags = {"anime", "spells", "sounds", "spec"},
        internalName = "anime-chant")
public class AnimeChantPlugin extends Plugin {


    @Inject
    private Client client;

    @Inject
    private EventBus eventBus;

    @Inject
    private ScheduledExecutorService executor; // RuneLite's shared pool

    @Inject
    private ClientThread clientThread;

    @Inject
    private AnimeChantConfig config;

    @Inject
    private SoundEngine soundEngine;

    @Inject
    private OverheadTextService overheadTextService;

    @Inject
    private SpellPlugin spellPlugin;

    @Inject
    private SpecialAttackPlugin specialAttackPlugin;

    @Override
    public void configure(Binder binder) {
        binder.install(new AnimeChantModule());
    }

    @Provides
    AnimeChantConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AnimeChantConfig.class);
    }

    @Override
    protected void startUp() {
        eventBus.register(spellPlugin);
        spellPlugin.startUp();

        eventBus.register(specialAttackPlugin);
        specialAttackPlugin.startUp();

        executor.execute(() -> {
            try {
                Filepath pluginDirectory = getPluginDirectory();
                pluginDirectory.createDirectories();
                log.debug("Anime chants plugin directory: {}", pluginDirectory);
            } catch (Exception e) {
                log.error("Failed to initialize directory.", e);
            }
        });
    }

    @Override
    protected void shutDown() {
        eventBus.unregister(spellPlugin);
        eventBus.unregister(specialAttackPlugin);

        clientThread.invokeLater(this::clearText);

        spellPlugin.shutDown();
        specialAttackPlugin.shutDown();
    }


    @Subscribe
    public void onGameTick(GameTick tick) {
        overheadTextService.onGameTick();
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        Optional<TextEntry> resolvedText = spellPlugin.onAnimationChanged(event);
        if (resolvedText.isEmpty()) return;

        overheadTextService.displayText(resolvedText.get(), config.shoutSpells());

        if (config.spellSoundsEnabled()) {
            playSound(resolvedText);
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        Optional<TextEntry> resolvedText = specialAttackPlugin.onVarbitChanged(event);
        if (resolvedText.isEmpty()) return;

        overheadTextService.displayText(resolvedText.get(), config.shoutSpecialAttacks());

        if (config.specialAttackSoundsEnabled()) {
            playSound(resolvedText);
        }
    }


    private void playSound(Optional<TextEntry> textEntry) {
        Optional<String> soundFile = textEntry.flatMap(TextEntry::getSound);
        if (soundFile.isEmpty() || soundFile.get().isEmpty()) return;

        executor.execute(() -> {
            try {
                // TODO merge path with file one.
                soundEngine.playSound(getPluginDirectory(), soundFile.get());
            } catch (RuntimeException | IOException e) {
                log.error("Failed to play sound file: {}", soundFile.get(), e);
            }
        });

    }

    private void clearText() {
        Player me = client.getLocalPlayer();
        if (me != null) {
            me.setOverheadText(null);
        }
    }
}
