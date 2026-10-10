package com.aguscerdo.animechant.spells;

import com.aguscerdo.animechant.presets.TextEntry;
import com.google.inject.ImplementedBy;

import java.util.Optional;

/**
 * Converts a cast {@link Spell} into the text/sound entry to display, based on the user's
 * configured presets and display throttling. Knows nothing about how the cast was detected.
 */
@ImplementedBy(SpellTextResolverImpl.class)
public interface SpellTextResolver {

    /**
     * Reloads presets and clears all throttling state.
     */
    void reloadAll();

    /** Clears all data structures from memory */
    void clear();

    /**
     * Reload Spell to Text presets if the current config differs from the actual user config.
     */
    void reloadPresets();

    /**
     * Resolves a spell into the text to display IF it should be displayed based on the user configs.
     *
     * @param spell the cast spell
     * @return the text to display if any, or empty.
     */
    Optional<TextEntry> resolve(Spell spell);
}
