package com.aguscerdo.animechant.spells;

import com.google.inject.ImplementedBy;
import net.runelite.api.MenuAction;

import java.util.Optional;

/**
 * Detects which spell the player actually cast, for both manual casts (menu click followed by
 * an animation) and autocasts. Knows nothing about text, chantdata or display throttling.
 */
@ImplementedBy(SpellCastDetectorImpl.class)
public interface SpellCastDetector {

    /**
     * Clears any pending cast intent.
     */
    void reset();

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
    void processManualCastEventIntent(Spell spell, MenuAction action);

    /**
     * Resolves the manually cast spell once its animation occurs.
     *
     * @param animationId the animation the player started
     * @return the spell that was cast, or empty if the animation doesn't match a pending cast.
     */
    Optional<Spell> handleManualSpell(int animationId);

    /**
     * Resolves the autocast spell for the current animation.
     *
     * @return the spell that was autocast, or empty.
     */
    Optional<Spell> handleAutoCast();
}
