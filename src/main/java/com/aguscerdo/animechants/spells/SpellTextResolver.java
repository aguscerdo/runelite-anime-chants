package com.aguscerdo.animechants.spells;

import com.aguscerdo.animechants.chantdata.model.TextEntry;
import com.aguscerdo.animechants.spells.model.Spell;
import com.google.inject.ImplementedBy;

import java.util.Optional;

/**
 * Converts a cast {@link Spell} into the text/files entry to display, based on the user's
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
    void reloadAll(boolean force);

    /**
     * Reload Spell to Text chantdata if the current config differs from the actual user config.
     */
    void reloadPresets(boolean force);

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
