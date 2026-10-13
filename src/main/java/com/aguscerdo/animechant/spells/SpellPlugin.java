package com.aguscerdo.animechant.spells;

import com.aguscerdo.animechant.chantdata.model.TextEntry;
import com.google.inject.ImplementedBy;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;

import java.util.Optional;

/**
 * Handles Spell related plugin actions for Anime Chant.
 */
@ImplementedBy(SpellPluginImpl.class)
public interface SpellPlugin {

    void startUp();

    void shutDown();

    /**
     * Handles an animation change on an actor. If the local player started a
     * spell animation (manual or autocast) that resolves to chant text, the
     * text is shown as overhead text.
     */
    Optional<TextEntry> onAnimationChanged(AnimationChanged event);

    /**
     * Handles a menu option click, working out which spell (if any) the player
     * is about to cast and passing that intent on to the cast detector.
     */
    @Subscribe
    void onMenuOptionClicked(MenuOptionClicked event);

    /**
     * Handles config changes; reloads the spell text presets when the spell
     * preset setting of this plugin's config group changes.
     */
    @Subscribe
    void onConfigChanged(ConfigChanged event);
}