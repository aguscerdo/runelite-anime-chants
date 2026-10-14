package com.aguscerdo.animechant;

import com.aguscerdo.animechant.chantdata.model.TextEntry;
import com.aguscerdo.animechant.files.SoundEngine;
import com.aguscerdo.animechant.specials.SpecialAttackPlugin;
import com.aguscerdo.animechant.spells.SpellPlugin;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
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
    private SoundEngine soundEngine;


    @Mock
    @Bind
    private SpellPlugin mockSpellPlugin;

    @Mock
    @Bind
    private SpecialAttackPlugin mockSpecialAttackPlugin;

    @Mock
    @Bind
    private OverheadTextService mockOverheadTextService;

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

        verify(mockOverheadTextService, times(1)).onGameTick();
    }


    @Test
    public void onAnimationChanged_emptyTextEntry_nothing() {
        when(mockConfig.spellSoundsEnabled()).thenReturn(true);
        when(mockSpellPlugin.onAnimationChanged(any())).thenReturn(Optional.empty());

        plugin.onAnimationChanged(new AnimationChanged());

        verify(mockExecutor, never()).execute(any());
        verify(mockOverheadTextService, never()).displayText(any(), anyBoolean());
    }

    @Test
    public void onAnimationChanged_soundsDisabled_nothing() {
        when(mockConfig.spellSoundsEnabled()).thenReturn(false);
        when(mockSpellPlugin.onAnimationChanged(any())).thenReturn(Optional.of(TEXT_ENTRY));

        plugin.onAnimationChanged(new AnimationChanged());

        verify(mockExecutor, never()).execute(any());
    }

    @Test
    public void onAnimationChanged_emptySoundFile_nothing() {
        when(mockConfig.spellSoundsEnabled()).thenReturn(true);
        when(mockSpellPlugin.onAnimationChanged(any())).thenReturn(Optional.of(TEXT_ENTRY.withSound(Optional.empty())));

        plugin.onAnimationChanged(new AnimationChanged());

        verify(mockExecutor, never()).execute(any());
    }

    @Test
    public void onAnimationChanged_emptyTextSoundFile_nothing() {
        when(mockConfig.spellSoundsEnabled()).thenReturn(true);
        when(mockSpellPlugin.onAnimationChanged(any())).thenReturn(Optional.of(TEXT_ENTRY.withSound(Optional.of(""))));

        plugin.onAnimationChanged(new AnimationChanged());

        verify(mockExecutor, never()).execute(any());
    }


    @Test
    public void onAnimationChanged_soundFile_sentToExecutor() {
        when(mockConfig.spellSoundsEnabled()).thenReturn(true);
        when(mockSpellPlugin.onAnimationChanged(any())).thenReturn(Optional.of(TEXT_ENTRY));

        plugin.onAnimationChanged(new AnimationChanged());

        verify(mockExecutor, times(1)).execute(any());
    }


    @Test
    public void onAnimationChanged_textSentToOverheadService_sentToExecutor() {
        when(mockConfig.spellSoundsEnabled()).thenReturn(false);
        when(mockSpellPlugin.onAnimationChanged(any())).thenReturn(Optional.of(TEXT_ENTRY));

        plugin.onAnimationChanged(new AnimationChanged());

        verify(mockOverheadTextService, times(1)).displayText(any(), anyBoolean());
    }


}