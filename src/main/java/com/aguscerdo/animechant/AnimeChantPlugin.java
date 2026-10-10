package com.aguscerdo.animechant;

import com.aguscerdo.animechant.presets.TextEntry;
import com.aguscerdo.animechant.spells.SpellCastDetector;
import com.aguscerdo.animechant.spells.Spell;
import com.aguscerdo.animechant.spells.SpellTextResolver;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.Provides;

import java.util.*;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(name = "Anime Chant", description = "Shows custom text when you cast spells")
public class AnimeChantPlugin extends Plugin {


    private static final String CAST = "Cast";
    private static final String ARROW = "->";

    // Spells with alternative menu options that are not "Cast"
    private static final ImmutableMap<String, String> NON_CAST_ALT_NAMED_SPELLS = ImmutableMap.of(
            "Grand Exchange", "Grand Exchange Teleport",
            "Yanille", "Yanille Teleport"
    );

    private static final ImmutableSet<MenuAction> TARGETED_USES = ImmutableSet.of(
            MenuAction.WIDGET_TARGET_ON_NPC,
            MenuAction.WIDGET_TARGET_ON_PLAYER,
            MenuAction.WIDGET_TARGET_ON_GAME_OBJECT,
            MenuAction.WIDGET_TARGET_ON_GROUND_ITEM,
            MenuAction.WIDGET_TARGET_ON_WIDGET
    );

    @Inject
    private Client client;

    @Inject
    private AnimeChantConfig config;

    @Inject
    private SpellCastDetector castDetector;

    @Inject
    private SpellTextResolver textResolver;

    private int textTicksLeft;

    @Override
    public void configure(Binder binder) {
        binder.install(new AnimeChantModule());
    }

    @Provides
    AnimeChantConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AnimeChantConfig.class);
    }

    @Override
    protected void startUp() {
        textTicksLeft = 0;

        castDetector.reset();
        textResolver.reloadAll();
    }

    @Override
    protected void shutDown() {
        clearText();

        textTicksLeft = 0;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(AnimeChantConfig.GROUP)) {
            return;
        }

        // Gotta love Java's null safety :)
        if (Objects.equals(event.getKey(), AnimeChantConfig.SPELL_PRESET)) {
            textResolver.reloadPresets();
        }
    }

    /**
     * A click only records intent. Text is shown when the cast animation starts.
     */
    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        if (!config.spellsEnabled()) return;

        Spell spell = Spell.fromString(spellNameFromClick(event));
        MenuAction action = event.getMenuAction();

        castDetector.processManualCastEventIntent(spell, action);
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

        Optional<TextEntry> manualSpellText = castDetector.handleManualSpell(me.getAnimation()).flatMap(textResolver::resolve);
        if (manualSpellText.isPresent()) {
            displayText(manualSpellText.get());
            return;
        }

        // Manual spells did not match, try auto-cast.
        displayText(castDetector.handleAutoCast().flatMap(textResolver::resolve));
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        if (textTicksLeft > 0 && --textTicksLeft == 0) {
            clearText();
        }
    }


    /**
     * Returns the spell name for a click that selects/casts a spell, otherwise null.
     */
    private String spellNameFromClick(MenuOptionClicked event) {
        MenuAction action = event.getMenuAction();

        // A spell is selected and was just used on a target (NPC, item for alchemy, ...)
        if (TARGETED_USES.contains(action)) {
            Widget selected = client.getSelectedWidget();
            if (selected == null
                    || WidgetUtil.componentToInterface(selected.getId()) != InterfaceID.MAGIC_SPELLBOOK) {
                return "";
            }

            // Target looks like "High Level Alchemy -> Item"
            String target = Text.removeTags(event.getMenuTarget());
            int arrow = target.indexOf(ARROW);

            return (arrow > 0 ? target.substring(0, arrow) : target).trim();
        }

        // Non-targeted spells such as teleports
        String menuOption = event.getMenuOption();
        if (action == MenuAction.CC_OP
                && menuOption != null
                && WidgetUtil.componentToInterface(event.getParam1()) == InterfaceID.MAGIC_SPELLBOOK) {

            if (menuOption.equals(CAST)) {
                return Text.removeTags(event.getMenuTarget()).trim();
            }

            String spellRename = NON_CAST_ALT_NAMED_SPELLS.getOrDefault(menuOption, "");
            if (!spellRename.isEmpty()) {
                return spellRename;
            }
        }

        return "";
    }

    private void displayText(TextEntry textEntry) {
        if (textEntry == null || (textEntry.getText().isEmpty() && textEntry.getSound().isEmpty())) {
            return;
        }


        Player me = client.getLocalPlayer();
        if (me != null && !textEntry.getText().isBlank()) {
            String text = textEntry.getText();
            if (config.shoutSpells() && !(text.endsWith(".") || text.endsWith("!"))) {
                text += "!";
            }

            me.setOverheadText(text);
            textTicksLeft = config.durationTicks();
            log.debug("Showing text: {}", textEntry);
        }

        // TODO handle sound file.

    }

    private void displayText(Optional<TextEntry> text) {
        text.ifPresent(this::displayText);
    }

    private void clearText() {
        Player me = client.getLocalPlayer();
        if (me != null) {
            me.setOverheadText(null);
        }
    }

    private static final Set<MenuAction> NON_INTERRUPTING = EnumSet.of(
            MenuAction.EXAMINE_OBJECT, MenuAction.EXAMINE_NPC,
            MenuAction.EXAMINE_ITEM_GROUND,
            MenuAction.EXAMINE_WORLD_ENTITY,
            MenuAction.RUNELITE, MenuAction.RUNELITE_HIGH_PRIORITY,
            MenuAction.RUNELITE_LOW_PRIORITY, MenuAction.RUNELITE_OVERLAY,
            MenuAction.RUNELITE_OVERLAY_CONFIG, MenuAction.RUNELITE_INFOBOX,
            MenuAction.RUNELITE_PLAYER, MenuAction.RUNELITE_WIDGET
    );
}
