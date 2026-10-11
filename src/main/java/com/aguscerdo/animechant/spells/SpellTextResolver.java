package com.aguscerdo.animechant.spells;

import com.aguscerdo.animechant.chantdata.TextEntry;
import com.aguscerdo.animechant.spells.model.Spell;
import com.google.inject.ImplementedBy;

import java.util.Optional;

/**
 * Converts a cast {@link Spell} into the text/sound entry to display, based on the user's
 * configured chantdata and display throttling. Knows nothing about how the cast was detected.
 */
@ImplementedBy(SpellTextResolverImpl.class)
public interface SpellTextResolver {

    /**
     * Clears all data structures from memory
     */
    void clear();

    /**
     * Reloads chantdata and clears all throttling state.
     */
    void reloadAll();

    /**
     * Reload Spell to Text chantdata if the current config differs from the actual user config.
     */
    void reloadPresets();

    /**
     * Reloads spell custom overrides
     */
    void reloadOverrides();

    /**
     * Resolves a spell into the text to display IF it should be displayed based on the user configs.
     *
     * @param spell the cast spell
     * @return the text to display if any, or empty.
     */
    Optional<TextEntry> resolve(Spell spell);
}
