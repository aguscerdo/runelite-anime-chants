package com.aguscerdo.animechant;

import com.aguscerdo.animechant.chantdata.model.TextEntry;
import com.aguscerdo.animechant.specials.SpecialAttackPlugin;
import com.aguscerdo.animechant.spells.SpellPlugin;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarPlayerID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AnimeChantPluginTest {

    private static final TextEntry TEXT_ENTRY = new TextEntry("text", Optional.of("files.wav"));

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
    private ScheduledExecutorService mockExecutor;


    @Mock
    @Bind
    private SpellPlugin mockSpellPlugin;

    @Mock
    @Bind
    private SpecialAttackPlugin mockSpecialAttackPlugin;

    @Mock
    @Bind
    private TextAndSoundService mockTextAndSoundService;

    @Inject
    private AnimeChantPlugin plugin;


    @Before
    public void setUp() {
        Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);

        when(mockClient.getLocalPlayer()).thenReturn(mockPlayer);
    }

    @Test
    public void onGameTick_callOverheadTextService() {
        plugin.onGameTick(new GameTick());

        verify(mockTextAndSoundService, times(1)).onGameTick();
    }


    private VarbitChanged makeVarbitChanged(int value) {
        VarbitChanged varbit = new VarbitChanged();
        varbit.setVarpId(VarPlayerID.SA_ENERGY);
        varbit.setValue(value);

        return varbit;
    }


}