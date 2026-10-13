package com.aguscerdo.animechant;

import com.aguscerdo.animechant.chantdata.CustomOverrides;
import com.aguscerdo.animechant.chantdata.model.TextEntry;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class OverheadTextServiceImpl implements OverheadTextService {

    @Inject
    private Client client;

    @Inject
    private AnimeChantConfig config;

    private String ourText = "";
    private int ticksLeft;


    public void displayText(TextEntry entry, boolean shout) {
        if (entry == null) return;

        String text = entry.getText();
        if (text == null || text.isBlank() || text.equals(CustomOverrides.NONE)) return;

        Player me = client.getLocalPlayer();
        if (me == null) return;

        if (shout && !(text.endsWith(".") || text.endsWith("!"))) {
            text += "!";
        }

        me.setOverheadText(text);
        ourText = text;
        ticksLeft = config.durationTicks();
        log.debug("Showing text: {}", entry);
    }

    /**
     * Call once per GameTick. Clears the text exactly once, when the timer runs out.
     */
    public void onGameTick() {
        if (ticksLeft > 0 && --ticksLeft == 0) {
            clear();
        }
    }

    /**
     * Client thread only.
     */
    public void clear() {
        ticksLeft = 0;
        Player me = client.getLocalPlayer();
        if (me != null && !ourText.isEmpty() && ourText.equals(me.getOverheadText())) {
            me.setOverheadText(null);
        }
        ourText = "";
    }
}