package com.aguscerdo.animechants;

import com.aguscerdo.animechants.chantdata.model.TextEntry;
import com.google.inject.ImplementedBy;

import java.util.Optional;

/**
 * Handles overhead text display and clear
 */
@ImplementedBy(TextAndSoundServiceImpl.class)
public interface TextAndSoundService {

    /**
     * Call once per GameTick. Clears the text exactly once, when the timer runs out.
     */
    void onGameTick();

    /**
     * Clear displayed text.
     */
    void clear();

    /**
     * Displays overhead text and play sounds file for a {@link TextEntry}.
     */
    void processTextEntry(TextEntry textEntry, boolean shoutText, boolean soundsEnabled);

    void processTextEntryOpt(Optional<TextEntry> textEntry, boolean shoutText, boolean soundsEnabled);


    /**
     * Display text over the user
     */
    void displayText(TextEntry entry, boolean shout);


    /**
     * Plays sound
     */
    void playSound(TextEntry textEntry);
}