package com.aguscerdo.animechant.specials;

import com.aguscerdo.animechant.chantdata.model.TextEntry;
import com.google.inject.ImplementedBy;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;

import java.util.Optional;

/**
 * Handles Spell related plugin actions for Anime Chant.
 */
@ImplementedBy(SpecialAttackPluginImpl.class)
public interface SpecialAttackPlugin {

    void startUp();

    void shutDown();

    /**
     * Reset spec to -1 on log-ins to avoid false-positives
     */
    void onGameStateChanged(GameStateChanged state);

    /**
     * Handles a varbit change for special attack.
     */
    Optional<TextEntry> onVarbitChanged(VarbitChanged e);


    /**
     * Handles config changes; reloads the spell text presets when the spell
     * preset setting of this plugin's config group changes.
     */
    @Subscribe
    void onConfigChanged(ConfigChanged event);
}