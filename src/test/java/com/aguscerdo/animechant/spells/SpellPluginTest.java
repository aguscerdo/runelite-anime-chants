package com.aguscerdo.animechant.spells;

import com.aguscerdo.animechant.AnimeChantConfig;
import com.aguscerdo.animechant.chantdata.TextEntry;
import com.aguscerdo.animechant.spells.model.Spell;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.events.ConfigChanged;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SpellPluginTest {

    private static final TextEntry HIGH_LEVEL_ALCHEMY_TEXT = new TextEntry("HLA", Optional.of("HLA.wav"));
    private static final TextEntry FIRE_WAVE_TEXT = new TextEntry("FW", Optional.of("FW.wav"));

    private static final int HIGH_LEVEL_ALCHEMY_ANIMATION_ID = 713;

    private static final int MAGIC_SPELLBOOK_INTERFACE_ID = InterfaceID.MAGIC_SPELLBOOK << 16;

    @Mock
    @Bind
    private Client mockClient;

    @Mock
    @Bind
    private Player mockPlayer;

    @Mock
    @Bind
    private AnimeChantConfig mockConfig;

    @Mock
    @Bind
    private SpellCastDetector mockCastDetector;

    @Mock
    @Bind
    private SpellTextResolver mockTextResolver;

    @Inject
    private SpellPluginImpl plugin;

    @Before
    public void setUp() {
        Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);

        when(mockConfig.spellsEnabled()).thenReturn(true);
        when(mockConfig.durationTicks()).thenReturn(3);
        when(mockClient.getLocalPlayer()).thenReturn(mockPlayer);

        when(mockPlayer.getAnimation()).thenReturn(HIGH_LEVEL_ALCHEMY_ANIMATION_ID);

        when(mockCastDetector.handleManualSpell(eq(HIGH_LEVEL_ALCHEMY_ANIMATION_ID))).thenReturn(Optional.of(Spell.HIGH_LEVEL_ALCHEMY));
        when(mockTextResolver.resolve(eq(Spell.HIGH_LEVEL_ALCHEMY))).thenReturn(Optional.of(HIGH_LEVEL_ALCHEMY_TEXT));
    }

    @Test
    public void onAnimationChanged_spellsDisabled_nothingHappens() {
        when(mockConfig.spellsEnabled()).thenReturn(false);

        plugin.onAnimationChanged(new AnimationChanged());

        verify(mockClient, never()).getLocalPlayer();
    }

    @Test
    public void onAnimationChanged_localPlayerNull_nothingHappens() {
        when(mockClient.getLocalPlayer()).thenReturn(null);

        plugin.onAnimationChanged(new AnimationChanged());

        verify(mockCastDetector, never()).handleManualSpell(anyInt());
    }

    @Test
    public void onAnimationChanged_actorNotPlayer_nothingHappens() {
        AnimationChanged event = new AnimationChanged();
        Player fakePlayer = mock(Player.class);
        event.setActor(fakePlayer);

        plugin.onAnimationChanged(event);

        verify(mockCastDetector, never()).handleManualSpell(anyInt());
    }

    @Test
    public void onAnimationChanged_noAnimation_nothingHappens() {
        when(mockPlayer.getAnimation()).thenReturn(-1);

        plugin.onAnimationChanged(makeAnimationEvent());

        verify(mockCastDetector, never()).handleManualSpell(anyInt());
    }


    @Test
    public void onAnimationChanged_manualAnimationMatches_display_text() {
        when(mockCastDetector.handleManualSpell(eq(HIGH_LEVEL_ALCHEMY_ANIMATION_ID))).thenReturn(Optional.of(Spell.HIGH_LEVEL_ALCHEMY));
        when(mockTextResolver.resolve(eq(Spell.HIGH_LEVEL_ALCHEMY))).thenReturn(Optional.of(HIGH_LEVEL_ALCHEMY_TEXT));

        plugin.onAnimationChanged(makeAnimationEvent());

        verify(mockPlayer, times(1)).setOverheadText(HIGH_LEVEL_ALCHEMY_TEXT.getText());
    }

    @Test
    public void onAnimationChanged_spellNotMatched_noText() {
        when(mockCastDetector.handleManualSpell(eq(HIGH_LEVEL_ALCHEMY_ANIMATION_ID))).thenReturn(Optional.empty());
        when(mockCastDetector.handleAutoCast()).thenReturn(Optional.empty());
        when(mockTextResolver.resolve(eq(Spell.HIGH_LEVEL_ALCHEMY))).thenReturn(Optional.of(HIGH_LEVEL_ALCHEMY_TEXT));

        plugin.onAnimationChanged(makeAnimationEvent());

        verify(mockPlayer, never()).setOverheadText(any());
    }

    @Test
    public void onAnimationChanged_spellMatchesButNotResolved_noText() {
        when(mockCastDetector.handleManualSpell(eq(HIGH_LEVEL_ALCHEMY_ANIMATION_ID))).thenReturn(Optional.of(Spell.HIGH_LEVEL_ALCHEMY));
        when(mockCastDetector.handleAutoCast()).thenReturn(Optional.empty());
        when(mockTextResolver.resolve(eq(Spell.HIGH_LEVEL_ALCHEMY))).thenReturn(Optional.empty());

        plugin.onAnimationChanged(makeAnimationEvent());

        verify(mockPlayer, never()).setOverheadText(any());
    }

    @Test
    public void onAnimationChanged_spellNotMatchButAutocastDoes_displayText() {
        when(mockCastDetector.handleManualSpell(anyInt())).thenReturn(Optional.empty());
        when(mockCastDetector.handleAutoCast()).thenReturn(Optional.of((Spell.FIRE_WAVE)));
        when(mockTextResolver.resolve(eq(Spell.FIRE_WAVE))).thenReturn(Optional.of(FIRE_WAVE_TEXT));

        plugin.onAnimationChanged(makeAnimationEvent());

        verify(mockPlayer, times(1)).setOverheadText(FIRE_WAVE_TEXT.getText());
    }

    @Test
    public void onAnimationChanged_autoCastMatchesButNotResolved_noText() {
        when(mockCastDetector.handleManualSpell(anyInt())).thenReturn(Optional.empty());
        when(mockCastDetector.handleAutoCast()).thenReturn(Optional.of((Spell.FIRE_WAVE)));
        when(mockTextResolver.resolve(eq(Spell.FIRE_WAVE))).thenReturn(Optional.empty());

        plugin.onAnimationChanged(makeAnimationEvent());

        verify(mockPlayer, never()).setOverheadText(FIRE_WAVE_TEXT.getText());
    }

    @Test
    public void onAnimationChanged_emptyText_notDisplayed() {
        when(mockCastDetector.handleManualSpell(eq(HIGH_LEVEL_ALCHEMY_ANIMATION_ID))).thenReturn(Optional.of(Spell.HIGH_LEVEL_ALCHEMY));
        when(mockTextResolver.resolve(eq(Spell.HIGH_LEVEL_ALCHEMY))).thenReturn(Optional.of(new TextEntry("", Optional.empty())));

        plugin.onAnimationChanged(makeAnimationEvent());

        verify(mockPlayer, never()).setOverheadText(any());
    }

    @Test
    public void onAnimationChanged_blankText_notDisplayed() {
        when(mockCastDetector.handleManualSpell(eq(HIGH_LEVEL_ALCHEMY_ANIMATION_ID))).thenReturn(Optional.of(Spell.HIGH_LEVEL_ALCHEMY));
        when(mockTextResolver.resolve(eq(Spell.HIGH_LEVEL_ALCHEMY))).thenReturn(Optional.of(new TextEntry("   ", Optional.empty())));

        plugin.onAnimationChanged(makeAnimationEvent());

        verify(mockPlayer, never()).setOverheadText(any());
    }

    @Test
    public void onAnimationChanged_noneText_notDisplayed() {
        when(mockCastDetector.handleManualSpell(eq(HIGH_LEVEL_ALCHEMY_ANIMATION_ID))).thenReturn(Optional.of(Spell.HIGH_LEVEL_ALCHEMY));
        when(mockTextResolver.resolve(eq(Spell.HIGH_LEVEL_ALCHEMY))).thenReturn(Optional.of(new TextEntry("NONE", Optional.empty())));

        plugin.onAnimationChanged(makeAnimationEvent());

        verify(mockPlayer, never()).setOverheadText(any());
    }

    @Test
    public void onAnimationChanged_playerBecomesNull_notDisplayed() {
        when(mockCastDetector.handleManualSpell(eq(HIGH_LEVEL_ALCHEMY_ANIMATION_ID))).thenReturn(Optional.of(Spell.HIGH_LEVEL_ALCHEMY));
        when(mockTextResolver.resolve(eq(Spell.HIGH_LEVEL_ALCHEMY))).thenReturn(Optional.of(HIGH_LEVEL_ALCHEMY_TEXT));
        when(mockClient.getLocalPlayer()).thenReturn(mockPlayer).thenReturn(null);

        plugin.onAnimationChanged(makeAnimationEvent());

        verify(mockPlayer, never()).setOverheadText(any());
    }

    @Test
    public void onAnimationChanged_exclaimText_displayExclamation() {
        when(mockCastDetector.handleManualSpell(eq(HIGH_LEVEL_ALCHEMY_ANIMATION_ID))).thenReturn(Optional.of(Spell.HIGH_LEVEL_ALCHEMY));
        when(mockTextResolver.resolve(eq(Spell.HIGH_LEVEL_ALCHEMY))).thenReturn(Optional.of(HIGH_LEVEL_ALCHEMY_TEXT));
        when(mockConfig.shoutSpells()).thenReturn(true);

        plugin.onAnimationChanged(makeAnimationEvent());

        verify(mockPlayer, times(1)).setOverheadText(HIGH_LEVEL_ALCHEMY_TEXT.getText() + "!");
    }

    @Test
    public void onAnimationChanged_exclaimTextAlreadyEndsOnExclamation_noSecondExclamation() {
        when(mockCastDetector.handleManualSpell(eq(HIGH_LEVEL_ALCHEMY_ANIMATION_ID))).thenReturn(Optional.of(Spell.HIGH_LEVEL_ALCHEMY));
        when(mockTextResolver.resolve(eq(Spell.HIGH_LEVEL_ALCHEMY))).thenReturn(Optional.of(new TextEntry("HI!", Optional.empty())));
        when(mockConfig.shoutSpells()).thenReturn(true);

        plugin.onAnimationChanged(makeAnimationEvent());

        verify(mockPlayer, times(1)).setOverheadText("HI!");
    }

    @Test
    public void onAnimationChanged_exclaimTextEndsOnPeriod_noExclamation() {
        when(mockCastDetector.handleManualSpell(eq(HIGH_LEVEL_ALCHEMY_ANIMATION_ID))).thenReturn(Optional.of(Spell.HIGH_LEVEL_ALCHEMY));
        when(mockTextResolver.resolve(eq(Spell.HIGH_LEVEL_ALCHEMY))).thenReturn(Optional.of(new TextEntry("HI...", Optional.empty())));
        when(mockConfig.shoutSpells()).thenReturn(true);

        plugin.onAnimationChanged(makeAnimationEvent());

        verify(mockPlayer, times(1)).setOverheadText("HI...");
    }

    @Test
    public void onMenuOptionClicked_spellsDisabled_nothing() {
        when(mockConfig.spellsEnabled()).thenReturn(false);

        plugin.onMenuOptionClicked(makeMenuOptionClicked());

        verify(mockCastDetector, never()).processManualCastEventIntent(any(), any());
    }


    @Test
    public void onMenuOptionClicked_targetedOnNullWidget_resolvesEmptySpell() {
        MenuOptionClicked menuOption = makeMenuOptionClicked();
        when(menuOption.getMenuEntry().getType()).thenReturn(MenuAction.WIDGET_TARGET_ON_WIDGET);
        when(mockClient.getSelectedWidget()).thenReturn(null);

        plugin.onMenuOptionClicked(menuOption);

        verify(mockCastDetector, times(1)).processManualCastEventIntent(eq(Spell.UNKNOWN), any());
    }


    @Test
    public void onMenuOptionClicked_targetedSelectionNotSpell_resolvesEmptySpell() {
        MenuOptionClicked menuOption = makeMenuOptionClicked();
        when(menuOption.getMenuEntry().getType()).thenReturn(MenuAction.WIDGET_TARGET_ON_WIDGET);
        Widget mockWidget = mock(Widget.class);
        when(mockWidget.getId()).thenReturn(123);
        when(mockClient.getSelectedWidget()).thenReturn(mockWidget);

        plugin.onMenuOptionClicked(menuOption);

        verify(mockCastDetector, times(1)).processManualCastEventIntent(eq(Spell.UNKNOWN), any());
    }


    @Test
    public void onMenuOptionClicked_targetedSpellWithArrow_trimsRestAndResolvesSpell() {
        MenuOptionClicked menuOption = makeMenuOptionClicked();
        when(menuOption.getMenuEntry().getType()).thenReturn(MenuAction.WIDGET_TARGET_ON_WIDGET);
        Widget mockWidget = mock(Widget.class);
        when(mockClient.getSelectedWidget()).thenReturn(mockWidget);
        when(mockWidget.getId()).thenReturn(MAGIC_SPELLBOOK_INTERFACE_ID);
        when(menuOption.getMenuEntry().getTarget()).thenReturn("High Level Alchemy -> you");


        plugin.onMenuOptionClicked(menuOption);

        verify(mockCastDetector, times(1)).processManualCastEventIntent(eq(Spell.HIGH_LEVEL_ALCHEMY), any());
    }


    @Test
    public void onMenuOptionClicked_targetedSpellWithoutArrow_trimsRestAndResolvesSpell() {
        MenuOptionClicked menuOption = makeMenuOptionClicked();
        when(menuOption.getMenuEntry().getType()).thenReturn(MenuAction.WIDGET_TARGET_ON_WIDGET);
        Widget mockWidget = mock(Widget.class);
        when(mockClient.getSelectedWidget()).thenReturn(mockWidget);
        when(mockWidget.getId()).thenReturn(MAGIC_SPELLBOOK_INTERFACE_ID);
        when(menuOption.getMenuEntry().getTarget()).thenReturn("High Level Alchemy");


        plugin.onMenuOptionClicked(menuOption);

        verify(mockCastDetector, times(1)).processManualCastEventIntent(eq(Spell.HIGH_LEVEL_ALCHEMY), any());
    }

    @Test
    public void onMenuOptionClicked_nonTargetedSpellNotCcOp_unresolvedSpell() {
        MenuOptionClicked menuOption = makeMenuOptionClicked();
        when(menuOption.getMenuEntry().getType()).thenReturn(MenuAction.CANCEL);

        plugin.onMenuOptionClicked(menuOption);

        verify(mockCastDetector, times(1)).processManualCastEventIntent(eq(Spell.UNKNOWN), any());
    }

    @Test
    public void onMenuOptionClicked_nonTargetedSpellNotOnSpellbook_unresolvedSpell() {
        MenuOptionClicked menuOption = makeMenuOptionClicked();
        when(menuOption.getMenuEntry().getType()).thenReturn(MenuAction.CC_OP);
        when(menuOption.getMenuEntry().getParam1()).thenReturn(123);

        plugin.onMenuOptionClicked(menuOption);

        verify(mockCastDetector, times(1)).processManualCastEventIntent(eq(Spell.UNKNOWN), any());
    }

    @Test
    public void onMenuOptionClicked_nonTargetedSpellOnSpellbookCast_resolvedSpell() {
        MenuOptionClicked menuOption = makeMenuOptionClicked();
        when(menuOption.getMenuEntry().getType()).thenReturn(MenuAction.CC_OP);
        when(menuOption.getMenuEntry().getParam1()).thenReturn(MAGIC_SPELLBOOK_INTERFACE_ID);
        when(menuOption.getMenuEntry().getOption()).thenReturn("Cast");
        when(menuOption.getMenuEntry().getTarget()).thenReturn("Varrock Teleport");

        plugin.onMenuOptionClicked(menuOption);

        verify(mockCastDetector, times(1)).processManualCastEventIntent(eq(Spell.VARROCK_TELEPORT), any());
    }


    @Test
    public void onMenuOptionClicked_nonTargetedSpellOnSpellbookAltNamed_resolvedSpell() {
        MenuOptionClicked menuOption = makeMenuOptionClicked();
        when(menuOption.getMenuEntry().getType()).thenReturn(MenuAction.CC_OP);
        when(menuOption.getMenuEntry().getParam1()).thenReturn(MAGIC_SPELLBOOK_INTERFACE_ID);
        when(menuOption.getMenuEntry().getOption()).thenReturn("Grand Exchange");
        when(menuOption.getMenuEntry().getTarget()).thenReturn("Varrock Teleport");

        plugin.onMenuOptionClicked(menuOption);

        verify(mockCastDetector, times(1)).processManualCastEventIntent(eq(Spell.GRAND_EXCHANGE_TELEPORT), any());
    }

    @Test
    public void onMenuOptionClicked_nonTargetedSpellOnSpellbookCastUnresolved_unresolvedSpell() {
        MenuOptionClicked menuOption = makeMenuOptionClicked();
        when(menuOption.getMenuEntry().getType()).thenReturn(MenuAction.CC_OP);
        when(menuOption.getMenuEntry().getParam1()).thenReturn(MAGIC_SPELLBOOK_INTERFACE_ID);
        when(menuOption.getMenuEntry().getOption()).thenReturn("Cast");
        when(menuOption.getMenuEntry().getTarget()).thenReturn("Alch yourself");

        plugin.onMenuOptionClicked(menuOption);

        verify(mockCastDetector, times(1)).processManualCastEventIntent(eq(Spell.UNKNOWN), any());
    }

    @Test
    public void textTicksLeft_onZero_minusOne() {
        assertEquals(plugin.textTicksLeft(), -1);
    }

    @Test
    public void textTicksLeft_displayedText_countsDown() {
        when(mockConfig.durationTicks()).thenReturn(3);
        when(mockCastDetector.handleManualSpell(eq(HIGH_LEVEL_ALCHEMY_ANIMATION_ID))).thenReturn(Optional.of(Spell.HIGH_LEVEL_ALCHEMY));
        when(mockTextResolver.resolve(eq(Spell.HIGH_LEVEL_ALCHEMY))).thenReturn(Optional.of(HIGH_LEVEL_ALCHEMY_TEXT));
        plugin.onAnimationChanged(makeAnimationEvent());

        assertEquals(plugin.textTicksLeft(), 2);
        assertEquals(plugin.textTicksLeft(), 1);
        assertEquals(plugin.textTicksLeft(), 0);
        assertEquals(plugin.textTicksLeft(), -1);
    }

    @Test
    public void onConfigChanged_notThisPlugin_ignore() {
        ConfigChanged configChanged = new ConfigChanged();
        configChanged.setGroup("Couldn't be me");
        configChanged.setKey(AnimeChantConfig.SPELL_PRESET);

        plugin.onConfigChanged(configChanged);

        verify(mockTextResolver, never()).reloadPresets();
    }

    @Test
    public void onConfigChanged_notPresets_ignore() {
        ConfigChanged configChanged = new ConfigChanged();
        configChanged.setGroup(AnimeChantConfig.GROUP);
        configChanged.setKey("Volume");

        plugin.onConfigChanged(configChanged);

        verify(mockTextResolver, never()).reloadPresets();
    }

    @Test
    public void onConfigChanged_presets_reload() {
        ConfigChanged configChanged = new ConfigChanged();
        configChanged.setGroup(AnimeChantConfig.GROUP);
        configChanged.setKey(AnimeChantConfig.SPELL_PRESET);

        plugin.onConfigChanged(configChanged);

        verify(mockTextResolver, times(1)).reloadPresets();
    }

    private AnimationChanged makeAnimationEvent() {
        AnimationChanged animation = new AnimationChanged();
        animation.setActor(mockPlayer);

        return animation;
    }

    private MenuOptionClicked makeMenuOptionClicked() {
        return new MenuOptionClicked(mock(MenuEntry.class));
    }
}