package com.aguscerdo.animechants.spells;

import com.aguscerdo.animechants.spells.model.Spell;
import com.google.inject.Guice;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Player;
import net.runelite.api.gameval.VarbitID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.Silent.class)
public class SpellCastDetectorTest {
    private static final int ALCHEMY_ANIMATION_ID = 713;
    private static final int OTHER_CAST_ANIMATION_ID = 1162;

    private static final int CURRENT_TICK = 1000;

    @Mock
    @Bind
    private Client mockClient;

    @Mock
    @Bind
    private Player mockPlayer;

    @Mock
    @Bind
    private Actor mockActor;

    private SpellCastDetectorImpl detector;

    @Before
    public void setUp() {
        detector = new SpellCastDetectorImpl();
        Guice.createInjector(BoundFieldModule.of(this)).injectMembers(detector);

        when(mockClient.getTickCount()).thenReturn(100);
        when(mockClient.getVarbitValue(eq(VarbitID.AUTOCAST_SPELL))).thenReturn(4);
        when(mockClient.getLocalPlayer()).thenReturn(mockPlayer);
        when(mockPlayer.getInteracting()).thenReturn(mockActor);
    }

    @Test
    public void reset_clearsPending() {
        detector.processManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.CC_OP);

        detector.reset();

        assertThat(detector.handleManualSpell(ALCHEMY_ANIMATION_ID)).isEmpty();
    }

    @Test
    public void handleManualSpell_withoutPendingCast_returnsEmpty() {
        assertThat(detector.handleManualSpell(ALCHEMY_ANIMATION_ID)).isEmpty();
    }

    @Test
    public void handleManualSpell_matchingAnimation_returnsSpell() {
        detector.processManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.WIDGET_TARGET_ON_NPC);

        assertThat(detector.handleManualSpell(ALCHEMY_ANIMATION_ID)).hasValue(Spell.HIGH_LEVEL_ALCHEMY);
    }

    @Test
    public void handleManualSpell_isConsumedAfterMatch() {
        detector.processManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.WIDGET_TARGET_ON_NPC);

        assertThat(detector.handleManualSpell(ALCHEMY_ANIMATION_ID)).isPresent();
        assertThat(detector.handleManualSpell(ALCHEMY_ANIMATION_ID)).isEmpty();
    }

    @Test
    public void handleManualSpell_mismatchedAnimation_returnsEmptyButKeepsPending() {
        detector.processManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.CC_OP);

        assertThat(detector.handleManualSpell(OTHER_CAST_ANIMATION_ID)).isEmpty();
        assertThat(detector.handleManualSpell(ALCHEMY_ANIMATION_ID)).hasValue(Spell.HIGH_LEVEL_ALCHEMY);
    }

    @Test
    public void handleManualSpell_expiredCast_returnsEmptyAndClearsPending() {
        when(mockClient.getTickCount())
                .thenReturn(CURRENT_TICK)
                .thenReturn(CURRENT_TICK + 50);
        detector.processManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.CC_OP);

        assertThat(detector.handleManualSpell(ALCHEMY_ANIMATION_ID)).isEmpty();
    }

    @Test
    public void handleManualSpell_atExactExpiryTick_stillResolves() {
        when(mockClient.getTickCount()).thenReturn(CURRENT_TICK + Spell.INSTANT_TIMEOUT_TICKS);
        detector.processManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.CC_OP);

        assertThat(detector.handleManualSpell(ALCHEMY_ANIMATION_ID)).isPresent();
    }


    @Test
    public void processManualCastEventIntent_unknownInterruptingAction_clearsPending() {
        detector.processManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.CC_OP);

        detector.processManualCastEventIntent(Spell.UNKNOWN, MenuAction.WALK);

        assertThat(detector.handleManualSpell(ALCHEMY_ANIMATION_ID)).isEmpty();
    }

    @Test
    public void processManualCastEventIntent_unknownNonInterruptingAction_keepsPending() {
        detector.processManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.CC_OP);

        detector.processManualCastEventIntent(Spell.UNKNOWN, MenuAction.EXAMINE_NPC);
        detector.processManualCastEventIntent(Spell.UNKNOWN, MenuAction.RUNELITE);

        assertThat(detector.handleManualSpell(ALCHEMY_ANIMATION_ID)).isPresent();
    }

    @Test
    public void processManualCastEventIntent_cancelWithoutSelectedWidget_keepsPending() {
        when(mockClient.isWidgetSelected()).thenReturn(false);
        detector.processManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.CC_OP);

        detector.processManualCastEventIntent(Spell.UNKNOWN, MenuAction.CANCEL);

        assertThat(detector.handleManualSpell(ALCHEMY_ANIMATION_ID)).isPresent();
    }

    @Test
    public void processManualCastEventIntent_cancelWithSelectedWidget_clearsPending() {
        when(mockClient.isWidgetSelected()).thenReturn(true);
        detector.processManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.CC_OP);

        detector.processManualCastEventIntent(Spell.UNKNOWN, MenuAction.CANCEL);

        assertThat(detector.handleManualSpell(ALCHEMY_ANIMATION_ID)).isEmpty();
    }

    @Test
    public void processManualCastEventIntent_widgetTargetSelection_doesNotSetPendingCast() {
        detector.processManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.WIDGET_TARGET);

        assertThat(detector.handleManualSpell(ALCHEMY_ANIMATION_ID)).isEmpty();
    }

    // TODO evaluate if Widget Target should not invalidate depending on where it is.
    @Test
    public void processManualCastEventIntent_widgetTargetSelection_clearsExistingPending() {
        detector.processManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.CC_OP);
        detector.processManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.WIDGET_TARGET);

        assertThat(detector.handleManualSpell(ALCHEMY_ANIMATION_ID)).isEmpty();
    }

    @Test
    public void processManualCastEventIntent_newSpellReplacesPendingSpell() {
        detector.processManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.CC_OP);
        detector.processManualCastEventIntent(Spell.FIRE_BLAST, MenuAction.CC_OP);

        assertThat(detector.handleManualSpell(ALCHEMY_ANIMATION_ID)).isEmpty();
    }

    @Test
    public void handleAutoCast_success() {
        when(mockPlayer.getAnimation()).thenReturn(1162);

        assertThat(detector.handleAutoCast()).hasValue(Spell.FIRE_STRIKE);
    }

    @Test
    public void handleAutoCast_autocastVarbitIsZero_returnsEmpty() {
        when(mockClient.getVarbitValue(eq(VarbitID.AUTOCAST_SPELL))).thenReturn(0);

        assertThat(detector.handleAutoCast()).isEmpty();
    }

    @Test
    public void handleAutoCast_nullPlayer_returnsEmpty() {
        when(mockClient.getLocalPlayer()).thenReturn(null);

        assertThat(detector.handleAutoCast()).isEmpty();
    }

    @Test
    public void handleAutoCast_nullInteractingActor_returnsEmpty() {
        when(mockPlayer.getInteracting()).thenReturn(null);

        assertThat(detector.handleAutoCast()).isEmpty();
    }

    @Test
    public void handleAutoCast_unknownAutoCast_returnsEmpty() {
        when(mockClient.getVarbitValue(eq(VarbitID.AUTOCAST_SPELL))).thenReturn(999);

        assertThat(detector.handleAutoCast()).isEmpty();
    }

    @Test
    public void handleAutoCast_autocastAnimationDoesntMatch_returnsEmpty() {
        when(mockClient.getVarbitValue(eq(VarbitID.AUTOCAST_SPELL))).thenReturn(15);
        when(mockPlayer.getAnimation()).thenReturn(900);

        assertThat(detector.handleAutoCast()).isEmpty();
    }
}
