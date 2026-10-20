package com.aguscerdo.animechants;

import com.aguscerdo.animechants.chantdata.model.TextEntry;
import com.aguscerdo.animechants.files.FileManager;
import com.aguscerdo.animechants.specials.SpecialAttackPlugin;
import com.aguscerdo.animechants.spells.SpellPlugin;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Filepath;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@PluginDescriptor(
        name = "Anime Chant",
        description = "Shows custom text when you cast spells",
        tags = {"anime", "spells", "sounds", "spec"},
        internalName = "anime-chants")
public class AnimeChantsPlugin extends Plugin {
    private static final TextEntry RELOAD_TEXT = new TextEntry("Reloading all presets...", Optional.empty());

    @Inject
    private EventBus eventBus;

    @Inject
    private ScheduledExecutorService executor;

    @Inject
    private ClientThread clientThread;

    @Inject
    private TextAndSoundService textAndSoundService;

    @Inject
    private SpellPlugin spellPlugin;

    @Inject
    private SpecialAttackPlugin specialAttackPlugin;

    @Provides
    AnimeChantsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AnimeChantsConfig.class);
    }

    @Provides
    @Annotations.PluginFilePath
    Filepath providePluginFilePath() {
        try {
            return getPluginDirectory();
        } catch (IOException e) {
            log.warn("Failed to get plugin directory.");
            return null;
        }
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
                FileManager.copyPresetFiles(pluginDirectory, /* force= */false);
            } catch (Exception e) {
                log.error("Failed to initialize directories.", e);
            }
        });
    }

    @Override
    protected void shutDown() {
        eventBus.unregister(spellPlugin);
        eventBus.unregister(specialAttackPlugin);

        clientThread.invokeLater(textAndSoundService::clear);

        spellPlugin.shutDown();
        specialAttackPlugin.shutDown();
    }


    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(AnimeChantsConfig.GROUP)) return;

        // TODO figure out if there's a way for pop-up buttons and warnings.
        if (Objects.equals(event.getKey(), AnimeChantsConfig.RELOAD_PRESET_FILES)) {
            try {
                log.info("Reloading anime chant yaml preset files...");
                Filepath pluginDirectory = getPluginDirectory();
                executor.execute(
                        () -> {
                            textAndSoundService.displayText(RELOAD_TEXT, false);
                            FileManager.copyPresetFiles(pluginDirectory, /* force= */true);

                            spellPlugin.startUp();
                            specialAttackPlugin.startUp();
                        }
                );
            } catch (IOException e) {
                log.warn("Failed to get plugin directory for reload.");
            }
        }
    }


    @Subscribe
    public void onGameTick(GameTick tick) {
        textAndSoundService.onGameTick();
    }
}
