package com.aguscerdo.animechant.spells;

import com.aguscerdo.animechant.AnimeChantConfig;
import com.aguscerdo.animechant.TextPreset;
import com.aguscerdo.animechant.presets.PresetLoader;
import com.aguscerdo.animechant.presets.RawPresetData;
import com.aguscerdo.animechant.presets.TextEntry;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import net.runelite.api.*;
import net.runelite.api.gameval.VarbitID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link SpellChantResolver} (via {@link SpellChantResolverImpl}).
 * <p>
 * {@link Spell} and {@link TextEntry} are mocked so the tests don't depend on specific enum
 * constants or constructors. Mocking a final class/enum requires Mockito's inline mock maker
 * (the default since Mockito 5).
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class SpellChantResolverTest {
    private static final int AUTOCAST_ANIMATION_ID = 711;
    private static final int ALCHEMY_ANIMATION_ID = 713;
    private static final int TELEPORT_ANIMATION_ID = 713;

    private static final int OTHER_CAST_ANIMATION_ID = 1162;

    private static final TextEntry FIRE_STRIKE_TEXT = new TextEntry("Fire", Optional.empty());
    private static final TextEntry FIRE_TEXT = new TextEntry("Fire Category", Optional.empty());

    private static final TextEntry HIGH_LEVEL_ALCHEMY_TEXT = new TextEntry("Money", Optional.empty());
    private static final TextEntry TELEPORT_TEXT = new TextEntry("Teleport", Optional.empty());

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

    @Mock
    @Bind
    private PresetLoader mockPresetLoader;

    @Mock
    @Bind
    private AnimeChantConfig config;

    private ListMultimap<Spell, TextEntry> spellToText;
    private ListMultimap<SpellCategory, TextEntry> categoryToText;

    private SpellChantResolverImpl resolver;

    private TextEntry spellEntry;

    @Before
    public void setUp() {
        spellToText = ArrayListMultimap.create();
        spellToText.put(Spell.HIGH_LEVEL_ALCHEMY, HIGH_LEVEL_ALCHEMY_TEXT);
        spellToText.put(Spell.FIRE_STRIKE, FIRE_STRIKE_TEXT);
        spellToText.put(Spell.VARROCK_TELEPORT, TELEPORT_TEXT);

        categoryToText = ArrayListMultimap.create();
        categoryToText.put(SpellCategory.FIRE, FIRE_TEXT);

        injectResolver(TextPreset.NONE);

        when(mockClient.getTickCount()).thenReturn(100);
        when(mockClient.getVarbitValue(eq(VarbitID.AUTOCAST_SPELL))).thenReturn(4);
        when(mockClient.getLocalPlayer()).thenReturn(mockPlayer);
        when(mockPlayer.getInteracting()).thenReturn(mockActor);
    }

    private void injectResolver(TextPreset textPreset) {
        Injector injector = Guice.createInjector(BoundFieldModule.of(this));
        resolver = new SpellChantResolverImpl(textPreset, spellToText, categoryToText);

        injector.injectMembers(resolver);
    }

    private void rebuildResolver(TextPreset preset) {
        resolver = new SpellChantResolverImpl(preset, spellToText, categoryToText);
    }


    @Test
    public void handleManualSpell_withoutPendingCast_returnsEmpty() {
        assertThat(resolver.handleManualSpell(ALCHEMY_ANIMATION_ID)).isEmpty();
    }

    @Test
    public void handleManualSpell_matchingAnimation_returnsSpellText() {
        resolver.handleManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.WIDGET_TARGET_ON_NPC);

        Optional<TextEntry> result = resolver.handleManualSpell(ALCHEMY_ANIMATION_ID);

        assertThat(result).hasValue(HIGH_LEVEL_ALCHEMY_TEXT);
    }

    @Test
    public void handleManualSpell_isConsumedAfterMatch() {
        resolver.handleManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.WIDGET_TARGET_ON_NPC);

        assertThat(resolver.handleManualSpell(ALCHEMY_ANIMATION_ID)).isPresent();
        assertThat(resolver.handleManualSpell(ALCHEMY_ANIMATION_ID)).isEmpty();
    }

    @Test
    public void handleManualSpell_mismatchedAnimation_returnsEmptyButKeepsPending() {
        resolver.handleManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.CC_OP);

        assertThat(resolver.handleManualSpell(OTHER_CAST_ANIMATION_ID)).isEmpty();
        assertThat(resolver.handleManualSpell(ALCHEMY_ANIMATION_ID)).hasValue(HIGH_LEVEL_ALCHEMY_TEXT);
    }

    @Test
    public void handleManualSpell_expiredCast_returnsEmptyAndClearsPending() {
        when(mockClient.getTickCount())
                .thenReturn(CURRENT_TICK)
                .thenReturn(CURRENT_TICK + 50);
        resolver.handleManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.CC_OP);

        assertThat(resolver.handleManualSpell(ALCHEMY_ANIMATION_ID)).isEmpty();
    }

    @Test
    public void handleManualSpell_atExactExpiryTick_stillResolves() {
        when(mockClient.getTickCount()).thenReturn(CURRENT_TICK + Spell.INSTANT_TIMEOUT_TICKS);
        resolver.handleManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.CC_OP);

        assertThat(resolver.handleManualSpell(ALCHEMY_ANIMATION_ID)).isPresent();
    }

    @Test
    public void handleManualCastEventIntent_unknownInterruptingAction_clearsPending() {
        resolver.handleManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.CC_OP);

        resolver.handleManualCastEventIntent(Spell.UNKNOWN, MenuAction.WALK);

        assertThat(resolver.handleManualSpell(ALCHEMY_ANIMATION_ID)).isEmpty();
    }

    @Test
    public void handleManualCastEventIntent_unknownNonInterruptingAction_keepsPending() {
        resolver.handleManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.CC_OP);

        resolver.handleManualCastEventIntent(Spell.UNKNOWN, MenuAction.EXAMINE_NPC);
        resolver.handleManualCastEventIntent(Spell.UNKNOWN, MenuAction.RUNELITE);

        assertThat(resolver.handleManualSpell(ALCHEMY_ANIMATION_ID)).isPresent();
    }

    @Test
    public void handleManualCastEventIntent_cancelWithoutSelectedWidget_keepsPending() {
        when(mockClient.isWidgetSelected()).thenReturn(false);
        resolver.handleManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.CC_OP);

        resolver.handleManualCastEventIntent(Spell.UNKNOWN, MenuAction.CANCEL);

        assertThat(resolver.handleManualSpell(ALCHEMY_ANIMATION_ID)).isPresent();
    }

    @Test
    public void handleManualCastEventIntent_cancelWithSelectedWidget_clearsPending() {
        when(mockClient.isWidgetSelected()).thenReturn(true);
        resolver.handleManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.CC_OP);

        resolver.handleManualCastEventIntent(Spell.UNKNOWN, MenuAction.CANCEL);

        assertThat(resolver.handleManualSpell(ALCHEMY_ANIMATION_ID)).isEmpty();
    }

    @Test
    public void handleManualCastEventIntent_widgetTargetSelection_doesNotSetPendingCast() {
        resolver.handleManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.WIDGET_TARGET);

        assertThat(resolver.handleManualSpell(ALCHEMY_ANIMATION_ID)).isEmpty();
    }

    // TODO evaluate if Widget Target should not invalidate depending on where it is.
    @Test
    public void handleManualCastEventIntent_widgetTargetSelection_clearsExistingPending() {
        resolver.handleManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.CC_OP);
        resolver.handleManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.WIDGET_TARGET);

        assertThat(resolver.handleManualSpell(ALCHEMY_ANIMATION_ID)).isEmpty();
    }

    @Test
    public void handleManualCastEventIntent_newSpellReplacesPendingSpell() {
        resolver.handleManualCastEventIntent(Spell.HIGH_LEVEL_ALCHEMY, MenuAction.CC_OP);
        resolver.handleManualCastEventIntent(Spell.FIRE_BLAST, MenuAction.CC_OP);

        assertThat(resolver.handleManualSpell(ALCHEMY_ANIMATION_ID)).isEmpty();
    }


    @Test
    public void handleAutoCast_success() {
        when(mockPlayer.getAnimation()).thenReturn(1162);

        assertThat(resolver.handleAutoCast()).hasValue(FIRE_STRIKE_TEXT);
    }

    @Test
    public void handleAutoCast_autocastVarbitisZero_returnsEmpty() {
        when(mockClient.getVarbitValue(eq(VarbitID.AUTOCAST_SPELL))).thenReturn(0);

        assertThat(resolver.handleAutoCast()).isEmpty();
    }


    @Test
    public void handleAutoCast_nullPlayer_returnsEmpty() {
        when(mockClient.getLocalPlayer()).thenReturn(null);

        assertThat(resolver.handleAutoCast()).isEmpty();
    }

    @Test
    public void handleAutoCast_nullInteractingActor_returnsEmpty() {
        when(mockPlayer.getInteracting()).thenReturn(null);

        assertThat(resolver.handleAutoCast()).isEmpty();
    }

    @Test
    public void handleAutoCast_unknownAutoCast_returnsEmpty() {
        when(mockClient.getVarbitValue(eq(VarbitID.AUTOCAST_SPELL))).thenReturn(999);

        assertThat(resolver.handleAutoCast()).isEmpty();
    }

    @Test
    public void handleAutoCast_autocastAnimationDoesntMatch_returnsEmpty() {
        when(mockClient.getVarbitValue(eq(VarbitID.AUTOCAST_SPELL))).thenReturn(15);
        when(mockPlayer.getAnimation()).thenReturn(900);

        assertThat(resolver.handleAutoCast()).isEmpty();
    }


    @Test
    public void processSpellToTextEntry_unknownSpell_returnsEmpty() {
        assertThat(resolver.processSpellToTextEntry(Spell.UNKNOWN)).isEmpty();
    }

    @Test
    public void processSpellToTextEntry_spellEntryTakesPriorityOverCategory() {
        assertThat(resolver.processSpellToTextEntry(Spell.FIRE_STRIKE)).hasValue(FIRE_STRIKE_TEXT);
    }

    @Test
    public void processSpellToTextEntry_fallsBackToCategoryEntry() {
        assertThat(resolver.processSpellToTextEntry(Spell.FIRE_WAVE)).hasValue(FIRE_TEXT);
    }

    @Test
    public void processSpellToTextEntry_noEntriesAndFallbackDisabled_returnsEmpty() {
        assertThat(resolver.processSpellToTextEntry(Spell.DARK_DEMONBANE)).isEmpty();
    }

    @Test
    public void processSpellToTextEntry_noEntriesWithFallbackEnabled_returnsSpellName() {
        when(config.fallbackToSpellName()).thenReturn(true);

        assertThat(resolver.processSpellToTextEntry(Spell.DARK_DEMONBANE)).hasValue(new TextEntry(Spell.DARK_DEMONBANE.getName(), Optional.empty()));
    }

    @Test
    public void processSpellToTextEntry_spellNamePreset_returnsSpellNameEntry() {
        injectResolver(TextPreset.SPELL_NAME);

        assertThat(resolver.processSpellToTextEntry(Spell.HIGH_LEVEL_ALCHEMY)).hasValue(new TextEntry(Spell.HIGH_LEVEL_ALCHEMY.getName(), Optional.empty()));
    }

    @Test
    public void processSpellToTextEntry_multipleVariants_returnsOneOfThem() {
        TextEntry otherTextEntry = new TextEntry("Fire 2", Optional.empty());
        spellToText.put(Spell.FIRE_STRIKE, otherTextEntry);
        injectResolver(TextPreset.NONE);

        for (int i = 0; i < 20; i++) {
            Optional<TextEntry> result = resolver.processSpellToTextEntry(Spell.FIRE_STRIKE);

            assertThat(result).isPresent();
            assertThat(result.get()).isIn(otherTextEntry, FIRE_STRIKE_TEXT);
        }
    }

    @Test
    public void processSpellToTextEntry_entryWithNoTextOrSound_returnsEmpty() {
        TextEntry otherTextEntry = new TextEntry("", Optional.empty());
        spellToText.put(Spell.WATER_WAVE, otherTextEntry);
        injectResolver(TextPreset.NONE);

        assertThat(resolver.processSpellToTextEntry(Spell.WATER_WAVE)).isEmpty();
    }


    @Test
    public void processSpellToTextEntry_withTextDelay_presentThenEmpty() {
        when(config.spellTextDelayTicks()).thenReturn(10);
        when(mockClient.getTickCount())
                .thenReturn(CURRENT_TICK)
                .thenReturn(CURRENT_TICK + 5);

        assertThat(resolver.processSpellToTextEntry(Spell.FIRE_STRIKE)).isPresent();
        assertThat(resolver.processSpellToTextEntry(Spell.FIRE_STRIKE)).isEmpty();
    }

    @Test
    public void processSpellToTextEntry_withTextDelay_presentAfterDelay() {
        when(config.spellTextDelayTicks()).thenReturn(10);
        when(mockClient.getTickCount())
                .thenReturn(CURRENT_TICK)
                .thenReturn(CURRENT_TICK + 12);

        assertThat(resolver.processSpellToTextEntry(Spell.FIRE_STRIKE)).isPresent();
        assertThat(resolver.processSpellToTextEntry(Spell.FIRE_STRIKE)).isPresent();
    }


    @Test
    public void processSpellToTextEntry_withLastCastDelay_noDisplay() {
        when(config.spellTextDelayFromLastCastTicks()).thenReturn(10);
        when(mockClient.getTickCount())
                .thenReturn(CURRENT_TICK)
                .thenReturn(CURRENT_TICK + 5)
                .thenReturn(CURRENT_TICK + 10)
                .thenReturn(CURRENT_TICK + 15)
                .thenReturn(CURRENT_TICK + 20)
                .thenReturn(CURRENT_TICK + 25)
                .thenReturn(CURRENT_TICK + 30);

        assertThat(resolver.processSpellToTextEntry(Spell.FIRE_STRIKE)).isPresent();
        assertThat(resolver.processSpellToTextEntry(Spell.FIRE_STRIKE)).isEmpty();
        assertThat(resolver.processSpellToTextEntry(Spell.FIRE_STRIKE)).isEmpty();
        assertThat(resolver.processSpellToTextEntry(Spell.FIRE_STRIKE)).isEmpty();
        assertThat(resolver.processSpellToTextEntry(Spell.FIRE_STRIKE)).isEmpty();
        assertThat(resolver.processSpellToTextEntry(Spell.FIRE_STRIKE)).isEmpty();
        assertThat(resolver.processSpellToTextEntry(Spell.FIRE_STRIKE)).isEmpty();
    }


    @Test
    public void processSpellToTextEntry_withLastCastDelay_presentAfterDelay() {
        when(config.spellTextDelayFromLastCastTicks()).thenReturn(10);
        when(mockClient.getTickCount())
                .thenReturn(CURRENT_TICK)
                .thenReturn(CURRENT_TICK + 5)
                .thenReturn(CURRENT_TICK + 50);

        assertThat(resolver.processSpellToTextEntry(Spell.FIRE_STRIKE)).isPresent();
        assertThat(resolver.processSpellToTextEntry(Spell.FIRE_STRIKE)).isEmpty();
        assertThat(resolver.processSpellToTextEntry(Spell.FIRE_STRIKE)).isPresent();
    }

    @Test
    public void reloadPresets_noPresetChange_nothing() {
        when(config.spellPreset()).thenReturn(TextPreset.NONE);

        resolver.reloadPresets();

        verify(mockPresetLoader, never()).loadPreset(any());
    }

    @Test
    public void reloadPresets_presetsReturnsEmpty_emptyMaps() {
        when(config.spellPreset()).thenReturn(TextPreset.FRIEREN);
        when(mockPresetLoader.loadPreset(any())).thenReturn(Optional.empty());

        assertThat(resolver.processSpellToTextEntry(Spell.FIRE_STRIKE)).isPresent();

        resolver.reloadPresets();

        assertThat(resolver.processSpellToTextEntry(Spell.FIRE_STRIKE)).isEmpty();
    }

    @Test
    public void reloadPresets_updateFromPresets() {
        when(config.spellPreset()).thenReturn(TextPreset.FRIEREN);
        when(mockPresetLoader.loadPreset(any())).thenReturn(Optional.of(
                new RawPresetData("Frieren",
                        ImmutableListMultimap.of(SpellCategory.WATER, FIRE_TEXT),
                        ImmutableListMultimap.of(Spell.FIRE_STRIKE, HIGH_LEVEL_ALCHEMY_TEXT)
                )
        ));

        assertThat(resolver.processSpellToTextEntry(Spell.FIRE_STRIKE)).hasValue(FIRE_STRIKE_TEXT);

        resolver.reloadPresets();

        assertThat(resolver.processSpellToTextEntry(Spell.FIRE_STRIKE)).hasValue(HIGH_LEVEL_ALCHEMY_TEXT);
        assertThat(resolver.processSpellToTextEntry(Spell.WATER_BLAST)).hasValue(FIRE_TEXT);
    }
}