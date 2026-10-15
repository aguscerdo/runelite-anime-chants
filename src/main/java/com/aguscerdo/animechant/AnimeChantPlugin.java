package com.aguscerdo.animechant;

import com.aguscerdo.animechant.chantdata.model.TextEntry;
import com.aguscerdo.animechant.files.FileManager;
import com.aguscerdo.animechant.specials.SpecialAttackPlugin;
import com.aguscerdo.animechant.spells.SpellPlugin;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarPlayerID;
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
        internalName = "anime-chant")
public class AnimeChantPlugin extends Plugin {
    private static TextEntry YAML_RELOAD = new TextEntry("Reloading all presets...", Optional.empty());


    @Inject
    private Client client;

    @Inject
    private EventBus eventBus;

    @Inject
    private ScheduledExecutorService executor;

    @Inject
    private ClientThread clientThread;

    @Inject
    private AnimeChantConfig config;

    @Inject
    private TextAndSoundService textAndSoundService;

    @Inject
    private SpellPlugin spellPlugin;

    @Inject
    private SpecialAttackPlugin specialAttackPlugin;

    @Provides
    AnimeChantConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AnimeChantConfig.class);
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
                FileManager.writePresetFiles(pluginDirectory, /* force= */false);
            } catch (Exception e) {
                log.error("Failed to initialize directories.", e);
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
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(AnimeChantConfig.GROUP)) return;

        // TODO figure out if there's a way for pop-up buttons and warnings.
        if (Objects.equals(event.getKey(), AnimeChantConfig.RELOAD_PRESET_FILES)) {
            try {
                log.info("Reloading anime chant yaml preset files...");
                Filepath pluginDirectory = getPluginDirectory();
                executor.execute(
                        () -> {
                            textAndSoundService.displayText(YAML_RELOAD, false);
                            FileManager.writePresetFiles(pluginDirectory, /* force= */true);

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


    // TODO consider injecting the sound service as singleton and handling these directly.
    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        Optional<TextEntry> resolvedText = spellPlugin.onAnimationChanged(event);
        if (resolvedText.isEmpty()) return;

        processTextEntry(resolvedText.get(), config.shoutSpells(), config.spellSoundsEnabled());
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (event.getVarpId() != VarPlayerID.SA_ENERGY) return;

        Optional<TextEntry> resolvedText = specialAttackPlugin.onVarbitChanged(event);
        if (resolvedText.isEmpty()) return;

        processTextEntry(resolvedText.get(), config.shoutSpecialAttacks(), config.specialAttackSoundsEnabled());
    }

    private void processTextEntry(TextEntry textEntry,
                                  boolean shout,
                                  boolean playSound) {
        textAndSoundService.displayText(textEntry, shout);

        if (playSound) {
            textAndSoundService.playSound(textEntry);
        }

    }

    private void clearText() {
        Player me = client.getLocalPlayer();
        if (me != null) {
            me.setOverheadText(null);
        }
    }
}
