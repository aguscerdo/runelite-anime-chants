package com.aguscerdo.animechants.specials;

import com.aguscerdo.animechants.chantdata.model.TextEntry;
import com.aguscerdo.animechants.specials.model.SpecWeapon;
import com.google.inject.ImplementedBy;

import java.util.Optional;

/**
 * Converts a cast {@link SpecWeapon} into the text/files entry to display, based on the user's
 * configured presets and overrides.
 */
@ImplementedBy(SpecialAttackTextResolverImpl.class)
public interface SpecialAttackTextResolver {

    /**
     * Clears all data structures from memory
     */
    void clear();

    /**
     * Reloads everything
     */
    void reloadAll(boolean force);

    /**
     * Reload spec weapon to Text data from presets if the current config differs from the actual user config.
     */
    void reloadPresets(boolean force);

    /**
     * Reloads spec weapon custom overrides
     */
    void reloadOverrides();

    /**
     * Resolves a {@link SpecWeapon} into the text to display IF it should be displayed based on the user configs.
     *
     * @param specWeapon the cast spell
     * @return the text to display if any, or empty.
     */
    Optional<TextEntry> resolve(SpecWeapon specWeapon);
}
