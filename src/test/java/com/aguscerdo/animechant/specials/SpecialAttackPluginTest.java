package com.aguscerdo.animechant.specials;

import com.aguscerdo.animechant.AnimeChantConfig;
import com.aguscerdo.animechant.chantdata.model.TextEntry;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.client.events.ConfigChanged;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SpecialAttackPluginTest {

    private static final int GRANITE_MAUL_ID = 4153;

    private static final TextEntry GRANITE_MAUL_TEXT_ENTRY = new TextEntry("Quickest of Smashes", Optional.of("gmaul.wav"));

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
    private SpecialAttackTextResolver mockTextResolver;

    @Mock
    private ItemContainer mockWornContainer;

    @Inject
    private SpecialAttackPluginImpl plugin;

    @Before
    public void setUp() {
        Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);

        when(mockConfig.specialAttacksEnabled()).thenReturn(true);
        when(mockConfig.durationTicks()).thenReturn(3);
        when(mockClient.getLocalPlayer()).thenReturn(mockPlayer);

        when(mockClient.getItemContainer(InventoryID.WORN)).thenReturn(mockWornContainer);
        when(mockWornContainer.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx()))
                .thenReturn(new Item(GRANITE_MAUL_ID, 1));
        when(mockTextResolver.resolve(any())).thenReturn(Optional.of(GRANITE_MAUL_TEXT_ENTRY));
    }

    @Test
    public void startUp_reloadsAll() {
        plugin.startUp();

        verify(mockTextResolver, times(1)).reloadAll();
    }

    @Test
    public void shutDown_clearsResolver() {
        plugin.shutDown();

        verify(mockTextResolver, times(1)).clear();
    }

    @Test
    public void shutDown_resetsLastSpec_ignore() {
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(1000))).isEmpty();

        plugin.shutDown();

        // Baseline was cleared, so this lower value only reseeds
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(500))).isEmpty();
    }

    @Test
    public void onConfigChanged_notThisPlugin_ignored() {
        ConfigChanged configChanged = new ConfigChanged();
        configChanged.setGroup("Couldn't be me");
        configChanged.setKey(AnimeChantConfig.SPELL_PRESET);

        plugin.onConfigChanged(configChanged);

        verify(mockTextResolver, never()).reloadOverrides();
        verify(mockTextResolver, never()).reloadPresets();
    }

    @Test
    public void onConfigChanged_unrelatedKey_ignored() {
        ConfigChanged configChanged = new ConfigChanged();
        configChanged.setGroup(AnimeChantConfig.GROUP);
        configChanged.setKey(AnimeChantConfig.SPELL_PRESET);

        plugin.onConfigChanged(configChanged);

        verify(mockTextResolver, never()).reloadOverrides();
        verify(mockTextResolver, never()).reloadPresets();
    }

    @Test
    public void onConfigChanged_presets_reload() {
        ConfigChanged configChanged = new ConfigChanged();
        configChanged.setGroup(AnimeChantConfig.GROUP);
        configChanged.setKey(AnimeChantConfig.SPECIAL_ATTACK_PRESET);

        plugin.onConfigChanged(configChanged);

        verify(mockTextResolver, times(1)).reloadPresets();
        verify(mockTextResolver, never()).reloadOverrides();
    }

    @Test
    public void onConfigChanged_overrides_reload() {
        ConfigChanged configChanged = new ConfigChanged();
        configChanged.setGroup(AnimeChantConfig.GROUP);
        configChanged.setKey(AnimeChantConfig.SPECIAL_ATTACK_OVERRIDE);

        plugin.onConfigChanged(configChanged);

        verify(mockTextResolver, times(1)).reloadOverrides();
        verify(mockTextResolver, never()).reloadPresets();
    }

    @Test
    public void onGameStateChanged_loginScreen_resetsLastSpec_ignore() {
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(1000))).isEmpty();
        plugin.onGameStateChanged(makeGameStateChanged(GameState.LOGIN_SCREEN));
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(500))).isEmpty();
    }

    @Test
    public void onGameStateChanged_hopping_resetsLastSpec_ignore() {
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(1000))).isEmpty();
        plugin.onGameStateChanged(makeGameStateChanged(GameState.HOPPING));
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(500))).isEmpty();
    }

    @Test
    public void onGameStateChanged_connectionLost_resetsLastSpec_ignore() {
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(1000))).isEmpty();
        plugin.onGameStateChanged(makeGameStateChanged(GameState.CONNECTION_LOST));
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(500))).isEmpty();
    }

    @Test
    public void onGameStateChanged_loggedIn_keepsLastSpec() {
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(1000))).isEmpty();
        plugin.onGameStateChanged(makeGameStateChanged(GameState.LOGGED_IN));
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(500))).contains(GRANITE_MAUL_TEXT_ENTRY);
    }

    @Test
    public void onGameStateChanged_loading_keepsLastSpec() {
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(1000))).isEmpty();
        plugin.onGameStateChanged(makeGameStateChanged(GameState.LOADING));
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(500))).contains(GRANITE_MAUL_TEXT_ENTRY);
    }

    @Test
    public void onGameStateChanged_loggedIn_doesNotReadVarp() {
        plugin.onGameStateChanged(makeGameStateChanged(GameState.LOGGED_IN));

        verify(mockClient, never()).getVarpValue(any(Integer.class));
    }

    @Test
    public void onVarbitChanged_notSpecialAttack_ignore() {
        assertThat(plugin.onVarbitChanged(new VarbitChanged())).isEmpty();
    }

    @Test
    public void onVarbitChanged_lastSpecMinusOne_seedsBaselineOnly() {
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(1000))).isEmpty();

        verify(mockTextResolver, never()).resolve(any());
    }

    @Test
    public void onVarbitChanged_specNotReduced_ignore() {
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(500))).isEmpty();
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(600))).isEmpty();
    }

    @Test
    public void onVarbitChanged_specUnchanged_ignore() {
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(500))).isEmpty();
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(500))).isEmpty();
    }

    @Test
    public void onVarbitChanged_specReduced_returnsText() {
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(1000))).isEmpty();
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(500))).contains(GRANITE_MAUL_TEXT_ENTRY);

        verify(mockTextResolver, times(1)).resolve(any());
    }

    @Test
    public void onVarbitChanged_specReducedTwice_returnsTextTwice() {
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(1000))).isEmpty();
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(500))).contains(GRANITE_MAUL_TEXT_ENTRY);
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(0))).contains(GRANITE_MAUL_TEXT_ENTRY);

        verify(mockTextResolver, times(2)).resolve(any());
    }

    @Test
    public void onVarbitChanged_specRegenThenReduced_returnsText() {
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(500))).isEmpty();
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(600))).isEmpty();
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(100))).contains(GRANITE_MAUL_TEXT_ENTRY);
    }

    @Test
    public void onVarbitChanged_afterRelog_firstValueSeedsThenDropTriggers() {
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(1000))).isEmpty();
        plugin.onGameStateChanged(makeGameStateChanged(GameState.LOGIN_SCREEN));
        plugin.onGameStateChanged(makeGameStateChanged(GameState.LOGGED_IN));

        assertThat(plugin.onVarbitChanged(makeVarbitChanged(700))).isEmpty();
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(200))).contains(GRANITE_MAUL_TEXT_ENTRY);
    }

    @Test
    public void onVarbitChanged_usesEventValue_notClientVarp() {
        // Client returns garbage; the plugin must rely on the event's value
        when(mockClient.getVarpValue(any(Integer.class))).thenReturn(-1);

        assertThat(plugin.onVarbitChanged(makeVarbitChanged(1000))).isEmpty();
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(500))).contains(GRANITE_MAUL_TEXT_ENTRY);
    }

    @Test
    public void onVarbitChanged_specReduced_noWornContainer_ignore() {
        when(mockClient.getItemContainer(InventoryID.WORN)).thenReturn(null);

        assertThat(plugin.onVarbitChanged(makeVarbitChanged(1000))).isEmpty();
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(500))).isEmpty();

        verify(mockTextResolver, never()).resolve(any());
    }

    @Test
    public void onVarbitChanged_specReduced_noWeapon_ignore() {
        when(mockWornContainer.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx())).thenReturn(null);

        assertThat(plugin.onVarbitChanged(makeVarbitChanged(1000))).isEmpty();
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(500))).isEmpty();

        verify(mockTextResolver, never()).resolve(any());
    }

    @Test
    public void onVarbitChanged_specReduced_resolverEmpty_ignore() {
        when(mockTextResolver.resolve(any())).thenReturn(Optional.empty());

        assertThat(plugin.onVarbitChanged(makeVarbitChanged(1000))).isEmpty();
        assertThat(plugin.onVarbitChanged(makeVarbitChanged(500))).isEmpty();

        verify(mockTextResolver, times(1)).resolve(any());
    }

    @Test
    public void onVarbitChanged_specNotReduced_resolverNotCalled() {
        plugin.onVarbitChanged(makeVarbitChanged(500));
        plugin.onVarbitChanged(makeVarbitChanged(600));

        verify(mockTextResolver, never()).resolve(any());
    }


    private VarbitChanged makeVarbitChanged(int value) {
        VarbitChanged varbit = new VarbitChanged();
        varbit.setVarpId(VarPlayerID.SA_ENERGY);
        varbit.setValue(value);

        return varbit;
    }

    private GameStateChanged makeGameStateChanged(GameState gameState) {
        GameStateChanged event = new GameStateChanged();
        event.setGameState(gameState);

        return event;
    }
}