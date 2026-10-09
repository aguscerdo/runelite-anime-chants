package com.animechant;

import com.animechant.spells.AutocastSpell;
import com.animechant.spells.Spell;
import com.animechant.spells.SpellKey;
import com.google.inject.Provides;

import java.util.*;
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
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.tuple.Pair;

@Slf4j
@PluginDescriptor(name = "Anime Chant", description = "Shows custom text when you cast spells")
public class AnimeChantPlugin extends Plugin {
    // A targeted spell may need us to walk into range first, so allow a long wait.
    private static final int TARGETED_TIMEOUT_TICKS = 30;

    // Teleports, alchemy etc. animate within a tick or two of the click.
    private static final int INSTANT_TIMEOUT_TICKS = 4;

    @Inject
    private Client client;

    @Inject
    private AnimeChantConfig config;

    @Inject
    private ChantResolver chantResolver;

    /**
     * A spell the player has clicked that hasn't been launched (animated) yet.
     */
    private static final class PendingCast {
        final Spell spell;
        final int expiresTick;

        PendingCast(Spell spell, int expiresTick) {
            this.spell = spell;
            this.expiresTick = expiresTick;
        }
    }


    private Optional<PendingCast> pendingCast = Optional.empty();

    // Tracking key ("spell:..." / "category:...") -> tick
    private final Map<SpellKey, Integer> lastCastTicks = new HashMap<>();
    private final Map<SpellKey, Integer> lastDisplayTicks = new HashMap<>();

    private int ticksLeft;

    @Provides
    AnimeChantConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AnimeChantConfig.class);
    }

    @Override
    protected void startUp() {
        ticksLeft = 0;
        pendingCast = Optional.empty();

        lastCastTicks.clear();
        lastDisplayTicks.clear();

        chantResolver.reloadPresets();
    }

    @Override
    protected void shutDown() {
        clearText();

        ticksLeft = 0;
        pendingCast = Optional.empty();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(AnimeChantConfig.GROUP)) {
            return;
        }

        // Gotta love Java's null sfety :)
        if (Objects.equals(event.getKey(), "spellPreset")) {
            chantResolver.reloadPresets();
        }
    }

    /**
     * A click only records intent. Text is shown when the cast animation starts.
     */
    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        if (!config.spellsEnabled()) {
            return;
        }

        MenuAction action = event.getMenuAction();
        String spellName = spellNameFromClick(event);
        Spell spell = Spell.fromString(spellName);

        if (!spell.isUnknown()) {
            int timeout = isTargetedUse(action) ? TARGETED_TIMEOUT_TICKS : INSTANT_TIMEOUT_TICKS;

            pendingCast = Optional.of(new PendingCast(spell, client.getTickCount() + timeout));
        } else if (action == MenuAction.WALK || action == MenuAction.CANCEL) {
            // Player walked away or cancelled, so they aren't casting after all.
            pendingCast = Optional.empty();
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if (!config.spellsEnabled() || event.getActor() != client.getLocalPlayer()) {
            return;
        }
        Player me = client.getLocalPlayer();
        if (me == null || me.getAnimation() == -1) {
            return;
        }

        // Manual cast (including alchemy, teleports, ...) takes priority over autocast.
        if (pendingCast.isPresent()) {
            pendingCast = Optional.empty();

            if (client.getTickCount() <= pendingCast.get().expiresTick) {
                handleSpell(pendingCast.get().spell);
                return;
            }
        }

        handleAutocast(me);
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        if (ticksLeft > 0 && --ticksLeft == 0) {
            clearText();
        }
    }


    private void handleAutocast(Player me) {
        int varbit = client.getVarbitValue(VarbitID.AUTOCAST_SPELL);
        if (varbit == 0 || !(me.getInteracting() instanceof NPC)) {
            return;
        }

        log.debug("Autocast animation {} with autocast varbit {}", me.getAnimation(), varbit);

        Spell spell = AutocastSpell.getSpell(varbit);
        if (spell.isUnknown()) {
            log.debug("Unmapped autocast varbit {}", varbit);
            return;
        }

        handleSpell(spell);
    }

    private void handleSpell(Spell spell) {
        Optional<Pair<String, SpellKey>> resolved = chantResolver.resolveSpell(spell);
        if (resolved.isEmpty()) {
            log.debug("No text for spell `{}`", spell);
            return;
        }

        String resolvedText = resolved.get().getLeft();
        SpellKey key = resolved.get().getRight();

        int tick = client.getTickCount();

        Integer lastCast = lastCastTicks.put(key, tick);
        Integer lastDisplay = lastDisplayTicks.getOrDefault(key, 0);

        int castDelay = config.spellTextDelayFromLastCastTicks();
        if (castDelay > 0 && lastCast != null && tick - lastCast < castDelay) {
            return;
        }

        int displayDelay = config.spellTextDelayTicks();
        if (displayDelay > 0 && lastDisplay != null && tick - lastDisplay < displayDelay) {
            return;
        }

        lastDisplayTicks.put(key, tick);
        displayText(resolvedText);
    }

    /**
     * Returns the spell name for a click that selects/casts a spell, otherwise null.
     */
    private String spellNameFromClick(MenuOptionClicked event) {
        MenuAction action = event.getMenuAction();

        // A spell is selected and was just used on a target (NPC, item for alchemy, ...)
        if (isTargetedUse(action)) {
            Widget selected = client.getSelectedWidget();
            if (selected == null
                    || WidgetUtil.componentToInterface(selected.getId()) != InterfaceID.MAGIC_SPELLBOOK) {
                return "";
            }

            // Target looks like "High Level Alchemy -> Item"
            String target = Text.removeTags(event.getMenuTarget());
            int arrow = target.indexOf("->");

            return (arrow > 0 ? target.substring(0, arrow) : target).trim();
        }

        // Non-targeted spells such as teleports
        if (action == MenuAction.CC_OP
                && event.getMenuOption().equals("Cast")
                && WidgetUtil.componentToInterface(event.getParam1()) == InterfaceID.MAGIC_SPELLBOOK) {

            return Text.removeTags(event.getMenuTarget()).trim();
        }

        return "";
    }

    private static boolean isTargetedUse(MenuAction action) {
        return action == MenuAction.WIDGET_TARGET_ON_NPC
                || action == MenuAction.WIDGET_TARGET_ON_PLAYER
                || action == MenuAction.WIDGET_TARGET_ON_GAME_OBJECT
                || action == MenuAction.WIDGET_TARGET_ON_GROUND_ITEM
                || action == MenuAction.WIDGET_TARGET_ON_WIDGET;
    }


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
        log.debug("Showing text: {}", text);
    }

    private void clearText() {
        Player me = client.getLocalPlayer();
        if (me != null) {
            me.setOverheadText(null);
        }
    }

    private static final Set<MenuAction> NON_INTERRUPTING = EnumSet.of(
            MenuAction.EXAMINE_OBJECT, MenuAction.EXAMINE_NPC,
            MenuAction.EXAMINE_ITEM_GROUND, MenuAction.EXAMINE_ITEM,
            MenuAction.EXAMINE_WORLD_ENTITY,
            MenuAction.RUNELITE, MenuAction.RUNELITE_HIGH_PRIORITY,
            MenuAction.RUNELITE_LOW_PRIORITY, MenuAction.RUNELITE_OVERLAY,
            MenuAction.RUNELITE_OVERLAY_CONFIG, MenuAction.RUNELITE_INFOBOX,
            MenuAction.RUNELITE_PLAYER, MenuAction.RUNELITE_WIDGET
    );
}
