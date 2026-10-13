package com.aguscerdo.animechant;

import com.aguscerdo.animechant.chantdata.model.TextEntry;
import com.google.inject.ImplementedBy;

/**
 * Handles overhead text display and clear
 */
@ImplementedBy(OverheadTextServiceImpl.class)
public interface OverheadTextService {

    /**
     * Display text over the user
     */
    void displayText(TextEntry entry, boolean shout);

    /**
     * Call once per GameTick. Clears the text exactly once, when the timer runs out.
     */
    void onGameTick();

    /**
     * Clear displayed text.
     */
    void clear();
}