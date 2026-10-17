package com.aguscerdo.animechant.spells;

import com.aguscerdo.animechant.AnimeChantConfig;
import com.aguscerdo.animechant.chantdata.PresetLoader;
import com.aguscerdo.animechant.chantdata.model.ChantData;
import com.aguscerdo.animechant.chantdata.model.TextEntry;
import com.aguscerdo.animechant.spells.model.Spell;
import com.aguscerdo.animechant.spells.model.SpellCategory;
import com.aguscerdo.animechant.spells.model.SpellTextPreset;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.inject.Guice;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import net.runelite.api.Client;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link SpellTextResolver} (via {@link SpellTextResolverImpl}).
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class SpellTextResolverTest {
    private static final TextEntry FIRE_STRIKE_TEXT = new TextEntry("Fire", Optional.empty());
    private static final TextEntry FIRE_TEXT = new TextEntry("Fire Category", Optional.empty());

    private static final TextEntry HIGH_LEVEL_ALCHEMY_TEXT = new TextEntry("Money", Optional.empty());
    private static final TextEntry TELEPORT_TEXT = new TextEntry("Teleport", Optional.empty());

    private static final TextEntry BAKE_PIE_TEXT = new TextEntry("Yummie pie", Optional.empty());
    private static final TextEntry DEMONBANE_TEXT = new TextEntry("Demons die", Optional.empty());


    private static final int CURRENT_TICK = 1000;

    @Mock
    @Bind
    private Client mockClient;

    @Mock
    @Bind
    private ScheduledExecutorService mockExecutor;

    @Mock
    @Bind
    private PresetLoader mockPresetLoader;

    @Mock
    @Bind
    private AnimeChantConfig config;

    private ListMultimap<Spell, TextEntry> spellToText;
    private ListMultimap<SpellCategory, TextEntry> categoryToText;

    ListMultimap<Spell, TextEntry> userSpellToText;
    ListMultimap<SpellCategory, TextEntry> userSpellCategoryToText;

    private SpellTextResolverImpl resolver;

    @Before
    public void setUp() {
        spellToText = ArrayListMultimap.create();
        spellToText.put(Spell.HIGH_LEVEL_ALCHEMY, HIGH_LEVEL_ALCHEMY_TEXT);
        spellToText.put(Spell.FIRE_STRIKE, FIRE_STRIKE_TEXT);
        spellToText.put(Spell.VARROCK_TELEPORT, TELEPORT_TEXT);

        categoryToText = ArrayListMultimap.create();
        categoryToText.put(SpellCategory.FIRE, FIRE_TEXT);

        userSpellToText = ArrayListMultimap.create();
        userSpellToText.put(Spell.BAKE_PIE, BAKE_PIE_TEXT);

        userSpellCategoryToText = ArrayListMultimap.create();
        userSpellCategoryToText.put(SpellCategory.DEMONBANE, DEMONBANE_TEXT);


        injectResolver(SpellTextPreset.NONE);

        when(mockClient.getTickCount()).thenReturn(100);
        doAnswer(invocation -> {
            Runnable command = invocation.getArgument(0);
            command.run();
            return null;
        }).when(mockExecutor).execute(any(Runnable.class));
    }

    private void injectResolver(SpellTextPreset spellTextPreset) {
        resolver = new SpellTextResolverImpl(spellTextPreset,
                spellToText,
                categoryToText,
                userSpellToText,
                userSpellCategoryToText
        );
        Guice.createInjector(BoundFieldModule.of(this)).injectMembers(resolver);
    }

    @Test
    public void resolve_unknownSpell_returnsEmpty() {
        assertThat(resolver.resolve(Spell.UNKNOWN)).isEmpty();
    }

    @Test
    public void resolve_spellEntryTakesPriorityOverCategory() {
        assertThat(resolver.resolve(Spell.FIRE_STRIKE)).hasValue(FIRE_STRIKE_TEXT);
    }

    @Test
    public void resolve_userSpellOverride() {
        assertThat(resolver.resolve(Spell.BAKE_PIE)).hasValue(BAKE_PIE_TEXT);
    }

    @Test
    public void resolve_fallsBackToCategoryEntry() {
        assertThat(resolver.resolve(Spell.FIRE_WAVE)).hasValue(FIRE_TEXT);
    }

    @Test
    public void resolve_userCategorySpellOverride() {
        assertThat(resolver.resolve(Spell.DARK_DEMONBANE)).hasValue(DEMONBANE_TEXT);
    }


    @Test
    public void resolve_userSpellOverridesPrioritizeOverPresets() {
        spellToText.put(Spell.BAKE_PIE, new TextEntry("I hate pies >:(", Optional.empty()));
        userSpellToText.put(Spell.BAKE_PIE, BAKE_PIE_TEXT);
        injectResolver(SpellTextPreset.NONE);

        assertThat(resolver.resolve(Spell.BAKE_PIE)).hasValue(BAKE_PIE_TEXT);
    }


    @Test
    public void resolve_userSpellCategoryOverridesPrioritizeOverPresets() {
        categoryToText.put(SpellCategory.DEMONBANE, new TextEntry("Demons are great", Optional.empty()));
        userSpellCategoryToText.put(SpellCategory.DEMONBANE, DEMONBANE_TEXT);
        injectResolver(SpellTextPreset.NONE);

        assertThat(resolver.resolve(Spell.DARK_DEMONBANE)).hasValue(DEMONBANE_TEXT);
    }

    @Test
    public void resolve_noEntriesAndFallbackDisabled_returnsEmpty() {
        assertThat(resolver.resolve(Spell.CLAWS_OF_GUTHIX)).isEmpty();
    }

    @Test
    public void resolve_noEntriesWithFallbackEnabled_returnsSpellName() {
        when(config.fallbackToSpellName()).thenReturn(true);

        assertThat(resolver.resolve(Spell.CLAWS_OF_GUTHIX))
                .hasValue(new TextEntry(Spell.CLAWS_OF_GUTHIX.getName(), Optional.empty()));
    }

    @Test
    public void resolve_multipleVariants_returnsOneOfThem() {
        TextEntry otherTextEntry = new TextEntry("Fire 2", Optional.empty());
        spellToText.put(Spell.FIRE_STRIKE, otherTextEntry);
        injectResolver(SpellTextPreset.NONE);

        for (int i = 0; i < 20; i++) {
            Optional<TextEntry> result = resolver.resolve(Spell.FIRE_STRIKE);

            assertThat(result).isPresent();
            assertThat(result.get()).isIn(otherTextEntry, FIRE_STRIKE_TEXT);
        }
    }

    @Test
    public void resolve_entryWithNoTextOrSound_returnsEmpty() {
        TextEntry otherTextEntry = new TextEntry("", Optional.empty());
        spellToText.put(Spell.WATER_WAVE, otherTextEntry);
        injectResolver(SpellTextPreset.NONE);

        assertThat(resolver.resolve(Spell.WATER_WAVE)).isEmpty();
    }

    @Test
    public void resolve_withTextDelay_presentThenEmpty() {
        when(config.spellTextDelayTicks()).thenReturn(10);
        when(mockClient.getTickCount())
                .thenReturn(CURRENT_TICK)
                .thenReturn(CURRENT_TICK + 5);

        assertThat(resolver.resolve(Spell.FIRE_STRIKE)).isPresent();
        assertThat(resolver.resolve(Spell.FIRE_STRIKE)).isEmpty();
    }

    @Test
    public void resolve_withTextDelay_presentAfterDelay() {
        when(config.spellTextDelayTicks()).thenReturn(10);
        when(mockClient.getTickCount())
                .thenReturn(CURRENT_TICK)
                .thenReturn(CURRENT_TICK + 12);

        assertThat(resolver.resolve(Spell.FIRE_STRIKE)).isPresent();
        assertThat(resolver.resolve(Spell.FIRE_STRIKE)).isPresent();
    }

    @Test
    public void resolve_withLastCastDelay_noDisplay() {
        when(config.spellTextDelayFromLastCastTicks()).thenReturn(10);
        when(mockClient.getTickCount())
                .thenReturn(CURRENT_TICK)
                .thenReturn(CURRENT_TICK + 5)
                .thenReturn(CURRENT_TICK + 10)
                .thenReturn(CURRENT_TICK + 15)
                .thenReturn(CURRENT_TICK + 20)
                .thenReturn(CURRENT_TICK + 25)
                .thenReturn(CURRENT_TICK + 30);

        assertThat(resolver.resolve(Spell.FIRE_STRIKE)).isPresent();
        assertThat(resolver.resolve(Spell.FIRE_STRIKE)).isEmpty();
        assertThat(resolver.resolve(Spell.FIRE_STRIKE)).isEmpty();
        assertThat(resolver.resolve(Spell.FIRE_STRIKE)).isEmpty();
        assertThat(resolver.resolve(Spell.FIRE_STRIKE)).isEmpty();
        assertThat(resolver.resolve(Spell.FIRE_STRIKE)).isEmpty();
        assertThat(resolver.resolve(Spell.FIRE_STRIKE)).isEmpty();
    }

    @Test
    public void resolve_withLastCastDelay_presentAfterDelay() {
        when(config.spellTextDelayFromLastCastTicks()).thenReturn(10);
        when(mockClient.getTickCount())
                .thenReturn(CURRENT_TICK)
                .thenReturn(CURRENT_TICK + 5)
                .thenReturn(CURRENT_TICK + 50);

        assertThat(resolver.resolve(Spell.FIRE_STRIKE)).isPresent();
        assertThat(resolver.resolve(Spell.FIRE_STRIKE)).isEmpty();
        assertThat(resolver.resolve(Spell.FIRE_STRIKE)).isPresent();
    }

    @Test
    public void reloadAll_clearsThrottleState() {
        when(config.spellPreset()).thenReturn(SpellTextPreset.NONE);
        when(config.spellTextDelayTicks()).thenReturn(10);
        when(mockClient.getTickCount()).thenReturn(CURRENT_TICK);

        assertThat(resolver.resolve(Spell.FIRE_STRIKE)).isPresent();
        assertThat(resolver.resolve(Spell.FIRE_STRIKE)).isEmpty();

        resolver.reloadAll(false);

        assertThat(resolver.resolve(Spell.FIRE_STRIKE)).isPresent();
    }

    @Test
    public void reloadPresets_noPresetChange_nothing() {
        when(config.spellPreset()).thenReturn(SpellTextPreset.NONE);

        resolver.reloadPresets(false);

        verify(mockPresetLoader, never()).loadPreset(any(SpellTextPreset.class));
    }

    @Test
    public void reloadPresets_presetsReturnsEmpty_emptyMaps() {
        when(config.spellPreset()).thenReturn(SpellTextPreset.FRIEREN);
        when(mockPresetLoader.loadPreset(any(SpellTextPreset.class))).thenReturn(Optional.empty());

        assertThat(resolver.resolve(Spell.FIRE_STRIKE)).isPresent();

        resolver.reloadPresets(false);

        assertThat(resolver.resolve(Spell.FIRE_STRIKE)).isEmpty();
    }

    @Test
    public void reloadPresets_updateFromPresets() {
        when(config.spellPreset()).thenReturn(SpellTextPreset.FRIEREN);
        when(mockPresetLoader.loadPreset(any(SpellTextPreset.class))).thenReturn(Optional.of(
                new ChantData(0.f, "Frieren",
                        "",
                        "",
                        ImmutableListMultimap.of(SpellCategory.WATER, FIRE_TEXT),
                        ImmutableListMultimap.of(Spell.FIRE_STRIKE, HIGH_LEVEL_ALCHEMY_TEXT),
                        ImmutableListMultimap.of()
                )
        ));

        assertThat(resolver.resolve(Spell.FIRE_STRIKE)).hasValue(FIRE_STRIKE_TEXT);

        resolver.reloadPresets(false);

        assertThat(resolver.resolve(Spell.FIRE_STRIKE)).hasValue(HIGH_LEVEL_ALCHEMY_TEXT);
        assertThat(resolver.resolve(Spell.WATER_BLAST)).hasValue(FIRE_TEXT);
    }
}
