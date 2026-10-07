package com.animechant;

import com.google.inject.Provides;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(name = "Anime Chant", description = "Shows custom text when you cast spells or use special attacks")
public class AnimeChantPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ItemManager itemManager;

    @Inject
    private AnimeChantConfig config;


    // Keep track of the last time text for a spell has been displayed
    private Map<SpellCategory, Integer> spellTextDisplayDelays = new HashMap<>();
    private Map<SpellCategory, Integer> spellCastDelays = new HashMap<>();

    // Varbit value -> spell name. UNVERIFIED: confirm with the debug log (see onAnimationChanged).
    // Anything not listed is reported as "Autocast #<value>" so it can still be mapped in Custom.
    private static final Map<Integer, String> AUTOCAST_SPELLS = new HashMap<>();

    static {
        String[] strikes = {"Wind Strike", "Water Strike", "Earth Strike", "Fire Strike"};
        String[] bolts = {"Wind Bolt", "Water Bolt", "Earth Bolt", "Fire Bolt"};
        String[] blasts = {"Wind Blast", "Water Blast", "Earth Blast", "Fire Blast"};
        String[] waves = {"Wind Wave", "Water Wave", "Earth Wave", "Fire Wave"};
        String[][] groups = {strikes, bolts, blasts, waves};
        int v = 1;
        for (String[] g : groups) {
            for (String name : g) {
                AUTOCAST_SPELLS.put(v++, name);
            }
        }
    }

    private int ticksLeft;
    private int lastShownTick = -10;

    @Provides
    AnimeChantConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AnimeChantConfig.class);
    }

    @Override
    protected void startUp() {
        ticksLeft = 0;
    }

    @Override
    protected void shutDown() {
        clearText();
        ticksLeft = 0;
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        if (!config.spellsEnabled()) {
            return;
        }

        String spellText = "";
        MenuAction action = event.getMenuAction();

        // TODO move this to a helper
        // TODO cover non-menu spells
        if (action == MenuAction.WIDGET_TARGET_ON_NPC
                || action == MenuAction.WIDGET_TARGET_ON_PLAYER
                || action == MenuAction.WIDGET_TARGET_ON_GAME_OBJECT
                || action == MenuAction.WIDGET_TARGET_ON_GROUND_ITEM
                || action == MenuAction.WIDGET_TARGET_ON_WIDGET) {
            // A spell is selected and was just used on a target
            Widget selected = client.getSelectedWidget();
            if (selected == null
                    || WidgetUtil.componentToInterface(selected.getId()) != InterfaceID.MAGIC_SPELLBOOK) {
                return;
            }
            // Target looks like "<col>Fire Strike</col><col> -> Goblin</col>"
            String target = Text.removeTags(event.getMenuTarget());
            int arrow = target.indexOf("->");
            spellText = (arrow > 0 ? target.substring(0, arrow) : target).trim();
        } else if (action == MenuAction.CC_OP
                && event.getMenuOption().equals("Cast")
                && WidgetUtil.componentToInterface(event.getParam1()) == InterfaceID.MAGIC_SPELLBOOK) {
            // Non-targeted spells such as teleports
            spellText = Text.removeTags(event.getMenuTarget()).trim();
        }

        if (spellText.isEmpty()) {
            return;
        }

        Spell spell = Spell.fromName(spellText);
        if (spell.isUnknown()) {
            log.debug("Skipping unknown spell `{}`", spellText);
            return;
        }

        handleSpell(spell);
    }

    public void handleSpell(Spell spell) {
        String spellText = config.spellPreset().spellText(spell.spellCategory);
        if (spellText.isEmpty()) {
            log.debug("Empty spell text for {} and config {}", spell.name, config.spellPreset());
            return;
        }

        boolean shouldDisplayText = true;
        int currentTick = client.getTickCount();

        int spellCastDelay = config.spellTextDelayFromLastCastTicks();
        if (spellCastDelay > 0) {
            int lastCast = this.spellCastDelays.getOrDefault(spell.spellCategory, 0);

            if (currentTick - lastCast < spellCastDelay) {
                shouldDisplayText = false;
            }

            this.spellCastDelays.put(spell.spellCategory, currentTick);
        }

        int spellDelay = config.spellTextDelayTicks();
        if (spellDelay > 0) {
            int lastDisplay = this.spellTextDisplayDelays.getOrDefault(spell.spellCategory, 0);

            if (currentTick - lastDisplay < spellDelay) {
                shouldDisplayText = false;
            }
        }

        if (!shouldDisplayText) {
            return;
        }

        if (spellDelay > 0) {
            this.spellTextDisplayDelays.put(spell.spellCategory, currentTick);
        }

        displayText(spellText);
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if (!config.spellsEnabled() || event.getActor() != client.getLocalPlayer()) {
            return;
        }

        Player me = client.getLocalPlayer();
        if (me.getAnimation() == -1) {
            return;
        }

        // TODO handle autocast enabled but cast another spell manually
        int autocast = client.getVarbitValue(VarbitID.AUTOCAST_SPELL);
        // Autocast must be set, we must be fighting an NPC, and a manual cast must not have just fired
        if (autocast == 0
                || !(me.getInteracting() instanceof NPC)
                || client.getTickCount() - lastShownTick < 2) {
            return;
        }

        log.debug("Autocast animation {} with autocast varbit {}", me.getAnimation(), autocast);

        // TODO figure out wtf this is.
        String spellText = AUTOCAST_SPELLS.getOrDefault(autocast, "Autocast #" + autocast);

        Spell spell = Spell.fromName(spellText);
        if (spell.isUnknown()) {
            log.debug("Skipping unknown spell `{}`", spellText);
            return;
        }

        handleSpell(spell);
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        if (ticksLeft > 0 && --ticksLeft == 0) {
            clearText();
        }
    }


    /**
     * Display local-only text over the NPC.
     */
    private void displayText(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }

        Player me = client.getLocalPlayer();
        if (me == null) {
            return;
        }

        me.setOverheadText(text);

        ticksLeft = config.durationTicks();
        lastShownTick = client.getTickCount();

        log.debug("Showing text: {}", text);
    }

    private void clearText() {
        Player me = client.getLocalPlayer();
        if (me != null) {
            me.setOverheadText(null);
        }
    }
}