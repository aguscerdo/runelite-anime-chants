package com.aguscerdo.animechant.spells;

import com.aguscerdo.animechant.presets.TextEntry;
import com.google.inject.ImplementedBy;
import net.runelite.api.MenuAction;

import java.util.Optional;

/**
 * Resolves spell casts (manual and autocast) into the text/sound entry to display,
 * based on the user's configured presets.
 */
@ImplementedBy(SpellChantResolverImpl.class)
public interface SpellChantResolver {

    /**
     * Reloads all the states of the Chant Resolver.
     */
    void reloadAll();

    /**
     * Reload Spell to Text presets if the current config differs from the actual user config.
     */
    void reloadPresets();

    /**
     * Processes a manual menu event to record, update or clear pending cast intent.
     * <p>
     * If a spell is cast it is stored as a pending spell action to be executed if its animation occurs.
     * If a non-spell breaking action occurs, the pending cast intent is cleared. This is important to avoid
     * mismatches between spells with the same animation.
     *
     * @param spell  the inferred spell from menu intents
     * @param action the menu action performed
     */
    void handleManualCastEventIntent(Spell spell, MenuAction action);

    /**
     * Resolves the text to display for a manually cast spell once its animation occurs.
     *
     * @param animationId the animation the player started
     * @return the text to display if any, or empty.
     */
    Optional<TextEntry> handleManualSpell(int animationId);

    /**
     * Resolves the text to display for an autocast spell.
     *
     * @return the text to display if any, or empty.
     */
    Optional<TextEntry> handleAutoCast();
}