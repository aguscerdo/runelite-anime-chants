package com.aguscerdo.animechant.spells;

import com.aguscerdo.animechant.AnimeChantConfig;
import com.aguscerdo.animechant.chantdata.CustomOverrides;
import com.aguscerdo.animechant.chantdata.TextEntry;
import com.aguscerdo.animechant.spells.model.Spell;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class SpellPluginImpl implements SpellPlugin {


    private static final String CAST_TEXT = "Cast";
    private static final String ARROW_TEXT = "->";

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

    @Provides
    AnimeChantConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AnimeChantConfig.class);
    }

    public void startUp() {
        textTicksLeft = 0;

        castDetector.reset();
        textResolver.reloadAll();
    }

    public void shutDown() {
        textTicksLeft = 0;

        textResolver.clear();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(AnimeChantConfig.GROUP)) {
            return;
        }

        if (Objects.equals(event.getKey(), AnimeChantConfig.SPELL_PRESET)) {
            textResolver.reloadPresets();
        }
        if (Objects.equals(event.getKey(), AnimeChantConfig.SPELL_OVERRIDES)) {
            textResolver.reloadOverrides();
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

    public Optional<TextEntry> onAnimationChanged(AnimationChanged event) {
        if (!config.spellsEnabled()) {
            return Optional.empty();
        }

        Player me = client.getLocalPlayer();
        if (me == null || event.getActor() != me || me.getAnimation() == -1) {
            return Optional.empty();
        }

        Optional<TextEntry> manualSpellText = castDetector.handleManualSpell(me.getAnimation()).flatMap(textResolver::resolve);
        if (manualSpellText.isPresent()) {
            displayText(manualSpellText.get());
            return Optional.empty();
        }

        // Manual spells did not match, try auto-cast.
        Optional<TextEntry> resolvedEntry = castDetector.handleAutoCast().flatMap(textResolver::resolve);
        displayText(resolvedEntry);

        return resolvedEntry;
    }

    /**
     * Reduces and returns the number of text ticks left.
     *
     * @return Number of ticks left if >= 0 or -1 otherwise.
     */
    public int textTicksLeft() {
        return textTicksLeft > 0 ? --textTicksLeft : -1;
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
            int arrow = target.indexOf(ARROW_TEXT);

            return (arrow > 0 ? target.substring(0, arrow) : target).trim();
        }

        // Non-targeted spells such as teleports
        String menuOption = event.getMenuOption();
        if (action == MenuAction.CC_OP
                && menuOption != null
                && WidgetUtil.componentToInterface(event.getParam1()) == InterfaceID.MAGIC_SPELLBOOK) {

            if (menuOption.equals(CAST_TEXT)) {
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
        if (me != null && !textEntry.getText().isBlank() && !textEntry.getText().equals(CustomOverrides.NONE)) {
            String text = textEntry.getText();
            if (config.shoutSpells() && !(text.endsWith(".") || text.endsWith("!"))) {
                text += "!";
            }

            me.setOverheadText(text);
            textTicksLeft = config.durationTicks();
            log.debug("Showing text: {}", textEntry);
        }
    }

    private void displayText(Optional<TextEntry> text) {
        text.ifPresent(this::displayText);
    }

}
