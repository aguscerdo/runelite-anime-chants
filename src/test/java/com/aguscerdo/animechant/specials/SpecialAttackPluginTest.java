package com.aguscerdo.animechant.specials;

import com.aguscerdo.animechant.AnimeChantConfig;
import com.aguscerdo.animechant.TextAndSoundService;
import com.aguscerdo.animechant.chantdata.model.TextEntry;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.events.ConfigChanged;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

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
    @Bind
    private TextAndSoundService mockTextAndSoundService;

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

        verify(mockTextResolver, times(1)).reloadAll(anyBoolean());
    }

    @Test
    public void shutDown_clearsResolver() {
        plugin.shutDown();

        verify(mockTextResolver, times(1)).clear();
    }

    @Test
    public void shutDown_resetsLastSpec_ignore() {
        plugin.onVarbitChanged(makeSpecVarbitChanged(1000));

        plugin.shutDown();

        // Baseline was cleared, so this lower value only reseeds
        plugin.onVarbitChanged(makeSpecVarbitChanged(500));
        verify(mockTextAndSoundService, times(0)).processTextEntry(any(), anyBoolean(), anyBoolean());
    }

    @Test
    public void onConfigChanged_notThisPlugin_ignored() {
        ConfigChanged configChanged = new ConfigChanged();
        configChanged.setGroup("Couldn't be me");
        configChanged.setKey(AnimeChantConfig.SPELL_PRESET);

        plugin.onConfigChanged(configChanged);

        verify(mockTextResolver, times(0)).reloadOverrides();
        verify(mockTextResolver, times(0)).reloadPresets(anyBoolean());
    }

    @Test
    public void onConfigChanged_unrelatedKey_ignored() {
        ConfigChanged configChanged = new ConfigChanged();
        configChanged.setGroup(AnimeChantConfig.GROUP);
        configChanged.setKey(AnimeChantConfig.SPELL_PRESET);

        plugin.onConfigChanged(configChanged);

        verify(mockTextResolver, times(0)).reloadOverrides();
        verify(mockTextResolver, times(0)).reloadPresets(anyBoolean());
    }

    @Test
    public void onConfigChanged_presets_reload() {
        ConfigChanged configChanged = new ConfigChanged();
        configChanged.setGroup(AnimeChantConfig.GROUP);
        configChanged.setKey(AnimeChantConfig.SPECIAL_ATTACK_PRESET);

        plugin.onConfigChanged(configChanged);

        verify(mockTextResolver, times(1)).reloadPresets(anyBoolean());
        verify(mockTextResolver, times(0)).reloadOverrides();
    }

    @Test
    public void onConfigChanged_overrides_reload() {
        ConfigChanged configChanged = new ConfigChanged();
        configChanged.setGroup(AnimeChantConfig.GROUP);
        configChanged.setKey(AnimeChantConfig.SPECIAL_ATTACK_OVERRIDE);

        plugin.onConfigChanged(configChanged);

        verify(mockTextResolver, times(1)).reloadOverrides();
        verify(mockTextResolver, times(0)).reloadPresets(anyBoolean());
    }

    @Test
    public void onGameStateChanged_loginScreen_resetsLastSpec_ignore() {
        plugin.onVarbitChanged(makeSpecVarbitChanged(1000));
        plugin.onGameStateChanged(makeGameStateChanged(GameState.LOGIN_SCREEN));
        plugin.onVarbitChanged(makeSpecVarbitChanged(500));

        verify(mockTextAndSoundService, times(0)).processTextEntry(any(), anyBoolean(), anyBoolean());
    }

    @Test
    public void onGameStateChanged_hopping_resetsLastSpec_ignore() {
        plugin.onVarbitChanged(makeSpecVarbitChanged(1000));
        plugin.onGameStateChanged(makeGameStateChanged(GameState.HOPPING));
        plugin.onVarbitChanged(makeSpecVarbitChanged(500));

        verify(mockTextAndSoundService, times(0)).processTextEntry(any(), anyBoolean(), anyBoolean());

    }

    @Test
    public void onGameStateChanged_connectionLost_resetsLastSpec_ignore() {
        plugin.onVarbitChanged(makeSpecVarbitChanged(1000));
        plugin.onGameStateChanged(makeGameStateChanged(GameState.CONNECTION_LOST));
        plugin.onVarbitChanged(makeSpecVarbitChanged(500));

        verify(mockTextAndSoundService, times(0)).processTextEntry(any(), anyBoolean(), anyBoolean());
    }

    @Test
    public void onGameStateChanged_loggedIn_keepsLastSpec() {
        plugin.onVarbitChanged(makeSpecVarbitChanged(1000));
        plugin.onGameStateChanged(makeGameStateChanged(GameState.LOGGED_IN));
        plugin.onVarbitChanged(makeSpecVarbitChanged(500));

        verify(mockTextAndSoundService, times(1)).processTextEntry(any(), anyBoolean(), anyBoolean());

    }

    @Test
    public void onGameStateChanged_loading_keepsLastSpec() {
        plugin.onVarbitChanged(makeSpecVarbitChanged(1000));
        plugin.onGameStateChanged(makeGameStateChanged(GameState.LOADING));
        plugin.onVarbitChanged(makeSpecVarbitChanged(500));

        verify(mockTextAndSoundService, times(1)).processTextEntry(any(), anyBoolean(), anyBoolean());

    }

    @Test
    public void onGameStateChanged_loggedIn_doesNotReadVarp() {
        plugin.onGameStateChanged(makeGameStateChanged(GameState.LOGGED_IN));

        verify(mockClient, times(0)).getVarpValue(any(Integer.class));
    }

    @Test
    public void onVarbitChanged_notSpecialAttack_ignore() {
        plugin.onVarbitChanged(new VarbitChanged());

        verify(mockTextResolver, times(0)).resolve(any());
    }

    @Test
    public void onVarbitChanged_lastSpecMinusOne_seedsBaselineOnly() {
        plugin.onVarbitChanged(makeSpecVarbitChanged(1000));

        verify(mockTextResolver, times(0)).resolve(any());
    }

    @Test
    public void onVarbitChanged_specNotReduced_ignore() {
        plugin.onVarbitChanged(makeSpecVarbitChanged(500));
        plugin.onVarbitChanged(makeSpecVarbitChanged(600));

        verify(mockTextResolver, times(0)).resolve(any());
    }

    @Test
    public void onVarbitChanged_specUnchanged_ignore() {
        plugin.onVarbitChanged(makeSpecVarbitChanged(500));
        plugin.onVarbitChanged(makeSpecVarbitChanged(500));

        verify(mockTextResolver, times(0)).resolve(any());
    }

    @Test
    public void onVarbitChanged_specReduced_success() {
        plugin.onVarbitChanged(makeSpecVarbitChanged(1000));
        plugin.onVarbitChanged(makeSpecVarbitChanged(500));

        verify(mockTextResolver, times(1)).resolve(any());
        verify(mockTextAndSoundService, times(1)).processTextEntry(any(), anyBoolean(), anyBoolean());
    }

    @Test
    public void onVarbitChanged_specialAttacksDisabled_stillModifiesCurrentSpec() {
        when(mockConfig.specialAttacksEnabled()).thenReturn(false)
                .thenReturn(false)
                .thenReturn(true);

        plugin.onVarbitChanged(makeSpecVarbitChanged(1000));
        plugin.onVarbitChanged(makeSpecVarbitChanged(500));
        plugin.onVarbitChanged(makeSpecVarbitChanged(0));

        verify(mockTextAndSoundService, times(1)).processTextEntry(any(), anyBoolean(), anyBoolean());
    }


    @Test
    public void onVarbitChanged_specReducedTwice_returnsTextTwice() {
        plugin.onVarbitChanged(makeSpecVarbitChanged(1000));
        plugin.onVarbitChanged(makeSpecVarbitChanged(500));
        plugin.onVarbitChanged(makeSpecVarbitChanged(0));

        verify(mockTextAndSoundService, times(2)).processTextEntry(any(), anyBoolean(), anyBoolean());
    }

    @Test
    public void onVarbitChanged_specRegenThenReduced_returnsText() {
        plugin.onVarbitChanged(makeSpecVarbitChanged(500));
        plugin.onVarbitChanged(makeSpecVarbitChanged(600));
        plugin.onVarbitChanged(makeSpecVarbitChanged(100));

        verify(mockTextAndSoundService, times(1)).processTextEntry(any(), anyBoolean(), anyBoolean());
    }

    @Test
    public void onVarbitChanged_afterRelog_firstValueSeedsThenDropTriggers() {
        plugin.onVarbitChanged(makeSpecVarbitChanged(1000));
        plugin.onGameStateChanged(makeGameStateChanged(GameState.LOGIN_SCREEN));
        plugin.onGameStateChanged(makeGameStateChanged(GameState.LOGGED_IN));

        plugin.onVarbitChanged(makeSpecVarbitChanged(700));
        plugin.onVarbitChanged(makeSpecVarbitChanged(200));

        verify(mockTextAndSoundService, times(1)).processTextEntry(any(), anyBoolean(), anyBoolean());
    }


    @Test
    public void onVarbitChanged_specReduced_noWornContainer_ignore() {
        when(mockClient.getItemContainer(InventoryID.WORN)).thenReturn(null);

        plugin.onVarbitChanged(makeSpecVarbitChanged(700));
        plugin.onVarbitChanged(makeSpecVarbitChanged(200));

        verify(mockTextResolver, times(0)).resolve(any());
    }

    @Test
    public void onVarbitChanged_specReduced_noWeapon_ignore() {
        when(mockWornContainer.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx())).thenReturn(null);

        plugin.onVarbitChanged(makeSpecVarbitChanged(1000));
        plugin.onVarbitChanged(makeSpecVarbitChanged(500));

        verify(mockTextResolver, times(0)).resolve(any());
    }

    @Test
    public void onVarbitChanged_specReduced_resolverEmpty_ignore() {
        when(mockTextResolver.resolve(any())).thenReturn(Optional.empty());

        plugin.onVarbitChanged(makeSpecVarbitChanged(1000));
        plugin.onVarbitChanged(makeSpecVarbitChanged(500));

        verify(mockTextResolver, times(1)).resolve(any());
        verify(mockTextAndSoundService, times(0)).processTextEntry(any(), anyBoolean(), anyBoolean());
    }


    @Test
    public void onVarbitChanged_altSpecNotRecognized_ignore() {
        plugin.onVarbitChanged(makeVarbit(123, 100));

        verify(mockTextResolver, times(0)).resolve(any());
        verify(mockTextAndSoundService, times(0)).processTextEntry(any(), anyBoolean(), anyBoolean());
    }


    @Test
    public void onVarbitChanged_altSpecConfigDisabled_ignore() {
        when(mockConfig.specialAttacksEnabled()).thenReturn(false);

        plugin.onVarbitChanged(makeVarbit(VarbitID.DRAGONFIRE_SHIELD_RECHARGE, 100));

        verify(mockTextResolver, times(0)).resolve(any());
        verify(mockTextAndSoundService, times(0)).processTextEntry(any(), anyBoolean(), anyBoolean());
    }


    @Test
    public void onVarbitChanged_altSpecNot24_ignore() {

        plugin.onVarbitChanged(makeVarbit(VarbitID.DRAGONFIRE_SHIELD_RECHARGE, 100));
        plugin.onVarbitChanged(makeVarbit(VarbitID.DRAGONFIRE_SHIELD_RECHARGE, 80));
        plugin.onVarbitChanged(makeVarbit(VarbitID.DRAGONFIRE_SHIELD_RECHARGE, 0));

        verify(mockTextResolver, times(0)).resolve(any());
        verify(mockTextAndSoundService, times(0)).processTextEntry(any(), anyBoolean(), anyBoolean());
    }


    @Test
    public void onVarbitChanged_altSpecNewNot24_success() {
        when(mockTextResolver.resolve(any())).thenReturn(Optional.of(GRANITE_MAUL_TEXT_ENTRY));

        plugin.onVarbitChanged(makeVarbit(VarbitID.DRAGONFIRE_SHIELD_RECHARGE, 0));
        plugin.onVarbitChanged(makeVarbit(VarbitID.DRAGONFIRE_SHIELD_RECHARGE, 23));

        verify(mockTextResolver, times(0)).resolve(any());
        verify(mockTextAndSoundService, times(0)).processTextEntry(any(), anyBoolean(), anyBoolean());
    }

    @Test
    public void onVarbitChanged_altNewVarbit24_success() {
        when(mockTextResolver.resolve(any())).thenReturn(Optional.of(GRANITE_MAUL_TEXT_ENTRY));

        plugin.onVarbitChanged(makeVarbit(VarbitID.DRAGONFIRE_SHIELD_RECHARGE, 0));
        plugin.onVarbitChanged(makeVarbit(VarbitID.DRAGONFIRE_SHIELD_RECHARGE, 24));

        verify(mockTextResolver, times(1)).resolve(any());
        verify(mockTextAndSoundService, times(1)).processTextEntry(any(), anyBoolean(), anyBoolean());
    }

    @Test
    public void onVarbitChanged_altSpecTextNotResolved_notDisplayed() {
        when(mockTextResolver.resolve(any())).thenReturn(Optional.empty());

        plugin.onVarbitChanged(makeVarbit(VarbitID.DRAGONFIRE_SHIELD_RECHARGE, 0));
        plugin.onVarbitChanged(makeVarbit(VarbitID.DRAGONFIRE_SHIELD_RECHARGE, 24));

        verify(mockTextResolver, times(1)).resolve(any());
        verify(mockTextAndSoundService, times(0)).processTextEntry(any(), anyBoolean(), anyBoolean());
    }

    @Test
    public void onVarbitChanged_sraDecreaseOfLessThanTwo_ignored() {
        plugin.onVarbitChanged(makePlayerVarbit(VarPlayerID.SOULREAPER_STACKS, 5));
        plugin.onVarbitChanged(makePlayerVarbit(VarPlayerID.SOULREAPER_STACKS, 4));

        verify(mockTextResolver, times(0)).resolve(any());
        verify(mockTextAndSoundService, times(0)).processTextEntry(any(), anyBoolean(), anyBoolean());
    }

    @Test
    public void onVarbitChanged_sraIncrease_ignored() {
        plugin.onVarbitChanged(makePlayerVarbit(VarPlayerID.SOULREAPER_STACKS, 4));
        plugin.onVarbitChanged(makePlayerVarbit(VarPlayerID.SOULREAPER_STACKS, 5));

        verify(mockTextResolver, times(0)).resolve(any());
        verify(mockTextAndSoundService, times(0)).processTextEntry(any(), anyBoolean(), anyBoolean());
    }

    @Test
    public void onVarbitChanged_sraDecreaseAllStacksNoTextEntry_ignore() {
        when(mockTextResolver.resolve(any())).thenReturn(Optional.empty());

        plugin.onVarbitChanged(makePlayerVarbit(VarPlayerID.SOULREAPER_STACKS, 4));
        plugin.onVarbitChanged(makePlayerVarbit(VarPlayerID.SOULREAPER_STACKS, 0));

        verify(mockTextResolver, times(1)).resolve(any());
        verify(mockTextAndSoundService, times(0)).processTextEntry(any(), anyBoolean(), anyBoolean());
    }

    @Test
    public void onVarbitChanged_sraDecreaseAllStacksWithTextEntry_display() {
        when(mockTextResolver.resolve(any())).thenReturn(Optional.of(GRANITE_MAUL_TEXT_ENTRY));

        plugin.onVarbitChanged(makePlayerVarbit(VarPlayerID.SOULREAPER_STACKS, 4));
        plugin.onVarbitChanged(makePlayerVarbit(VarPlayerID.SOULREAPER_STACKS, 0));

        verify(mockTextResolver, times(1)).resolve(any());
        verify(mockTextAndSoundService, times(1)).processTextEntry(any(), anyBoolean(), anyBoolean());
    }


    @Test
    public void onVarbitChanged_sraDecreaseNotTo0_ignored() {
        when(mockTextResolver.resolve(any())).thenReturn(Optional.empty());

        plugin.onVarbitChanged(makePlayerVarbit(VarPlayerID.SOULREAPER_STACKS, 4));
        plugin.onVarbitChanged(makePlayerVarbit(VarPlayerID.SOULREAPER_STACKS, 2));

        verify(mockTextResolver, times(0)).resolve(any());
        verify(mockTextAndSoundService, times(0)).processTextEntry(any(), anyBoolean(), anyBoolean());
    }

    private VarbitChanged makeSpecVarbitChanged(int value) {
        VarbitChanged varbit = new VarbitChanged();
        varbit.setVarpId(VarPlayerID.SA_ENERGY);
        varbit.setValue(value);

        return varbit;
    }

    private VarbitChanged makeVarbit(int varbitId, int value) {
        VarbitChanged varbit = new VarbitChanged();
        varbit.setVarbitId(varbitId);
        varbit.setValue(value);

        return varbit;
    }

    private VarbitChanged makePlayerVarbit(int varbitId, int value) {
        VarbitChanged varbit = new VarbitChanged();
        varbit.setVarpId(varbitId);
        varbit.setValue(value);

        return varbit;
    }


    private GameStateChanged makeGameStateChanged(GameState gameState) {
        GameStateChanged event = new GameStateChanged();
        event.setGameState(gameState);

        return event;
    }


    private ChatMessage makeChatMessage(String message) {
        return new ChatMessage(
                null, ChatMessageType.GAMEMESSAGE,
                "game", message, "", 12345
        );
    }
}