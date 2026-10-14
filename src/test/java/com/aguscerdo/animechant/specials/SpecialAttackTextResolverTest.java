package com.aguscerdo.animechant.specials;

import com.aguscerdo.animechant.AnimeChantConfig;
import com.aguscerdo.animechant.chantdata.PresetLoader;
import com.aguscerdo.animechant.chantdata.model.ChantData;
import com.aguscerdo.animechant.chantdata.model.TextEntry;
import com.aguscerdo.animechant.specials.model.SpecWeapon;
import com.aguscerdo.animechant.specials.model.SpecWeaponTextPreset;
import com.aguscerdo.animechant.specials.model.SpecialAttackFallback;
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
 * Tests for {@link SpecialAttackTextResolver} (via {@link SpecialAttackTextResolverImpl}).
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class SpecialAttackTextResolverTest {
    private static final TextEntry GRANITE_MAUL_TEXT = new TextEntry("Quickest of Smashes", Optional.of("gmaul.wav"));
    private static final TextEntry DRAGON_DAGGER_TEXT = new TextEntry("Double the stabs", Optional.empty());

    private static final TextEntry USER_DRAGON_CLAWS_TEXT = new TextEntry("Claw time", Optional.empty());

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

    private ListMultimap<SpecWeapon, TextEntry> specWeaponToText;
    private ListMultimap<SpecWeapon, TextEntry> userSpecWeaponToText;

    private SpecialAttackTextResolverImpl resolver;

    @Before
    public void setUp() {
        specWeaponToText = ArrayListMultimap.create();
        specWeaponToText.put(SpecWeapon.GRANITE_MAUL, GRANITE_MAUL_TEXT);
        specWeaponToText.put(SpecWeapon.DRAGON_DAGGER, DRAGON_DAGGER_TEXT);

        userSpecWeaponToText = ArrayListMultimap.create();
        userSpecWeaponToText.put(SpecWeapon.DRAGON_CLAWS, USER_DRAGON_CLAWS_TEXT);

        when(config.specialAttackFallback()).thenReturn(SpecialAttackFallback.NONE);

        doAnswer(invocation -> {
            Runnable command = invocation.getArgument(0);
            command.run();
            return null;
        }).when(mockExecutor).execute(any(Runnable.class));


        injectResolver(SpecWeaponTextPreset.NONE);
    }

    private void injectResolver(SpecWeaponTextPreset specWeaponTextPreset) {
        resolver = new SpecialAttackTextResolverImpl(specWeaponTextPreset,
                specWeaponToText,
                userSpecWeaponToText
        );
        Guice.createInjector(BoundFieldModule.of(this)).injectMembers(resolver);
    }

    @Test
    public void resolve_unknownSpecWeapon_returnsEmpty() {
        assertThat(resolver.resolve(SpecWeapon.UNKNOWN)).isEmpty();
    }

    @Test
    public void resolve_presetEntry() {
        assertThat(resolver.resolve(SpecWeapon.GRANITE_MAUL)).hasValue(GRANITE_MAUL_TEXT);
    }

    @Test
    public void resolve_userSpecWeaponOverride() {
        assertThat(resolver.resolve(SpecWeapon.DRAGON_CLAWS)).hasValue(USER_DRAGON_CLAWS_TEXT);
    }

    @Test
    public void resolve_userSpecWeaponOverridesPrioritizeOverPresets() {
        specWeaponToText.put(SpecWeapon.DRAGON_CLAWS, new TextEntry("Preset claws", Optional.empty()));
        userSpecWeaponToText.put(SpecWeapon.DRAGON_CLAWS, USER_DRAGON_CLAWS_TEXT);
        injectResolver(SpecWeaponTextPreset.NONE);

        assertThat(resolver.resolve(SpecWeapon.DRAGON_CLAWS)).hasValue(USER_DRAGON_CLAWS_TEXT);
    }

    @Test
    public void resolve_userSpecWeaponOverridesPrioritizeOverSpecialAttackNamePreset() {
        injectResolver(SpecWeaponTextPreset.ANIME_MIX);

        assertThat(resolver.resolve(SpecWeapon.DRAGON_CLAWS)).hasValue(USER_DRAGON_CLAWS_TEXT);
    }

    @Test
    public void resolve_noEntriesAndFallbackNone_returnsEmpty() {
        assertThat(resolver.resolve(SpecWeapon.ABYSSAL_WHIP)).isEmpty();
    }

    @Test
    public void resolve_noEntriesWithSpecialAttackNameFallback_returnsSpecialAttackName() {
        when(config.specialAttackFallback()).thenReturn(SpecialAttackFallback.SPECIAL_ATTACK_NAME);

        assertThat(resolver.resolve(SpecWeapon.ABYSSAL_WHIP))
                .hasValue(SpecWeapon.ABYSSAL_WHIP.specialAttackNameAsTextEntry());
    }

    @Test
    public void resolve_noEntriesWithWeaponNameFallback_returnsWeaponName() {
        when(config.specialAttackFallback()).thenReturn(SpecialAttackFallback.WEAPON_NAME);

        assertThat(resolver.resolve(SpecWeapon.ABYSSAL_WHIP))
                .hasValue(SpecWeapon.ABYSSAL_WHIP.nameAsTextEntry());
    }

    @Test
    public void resolve_presetEntryTakesPriorityOverFallback() {
        when(config.specialAttackFallback()).thenReturn(SpecialAttackFallback.WEAPON_NAME);

        assertThat(resolver.resolve(SpecWeapon.GRANITE_MAUL)).hasValue(GRANITE_MAUL_TEXT);
    }


    @Test
    public void resolve_multipleVariants_returnsOneOfThem() {
        TextEntry otherTextEntry = new TextEntry("Granite Maul 2", Optional.empty());
        specWeaponToText.put(SpecWeapon.GRANITE_MAUL, otherTextEntry);
        injectResolver(SpecWeaponTextPreset.NONE);

        for (int i = 0; i < 20; i++) {
            Optional<TextEntry> result = resolver.resolve(SpecWeapon.GRANITE_MAUL);

            assertThat(result).isPresent();
            assertThat(result.get()).isIn(otherTextEntry, GRANITE_MAUL_TEXT);
        }
    }

    @Test
    public void resolve_multipleUserVariants_returnsOneOfThem() {
        TextEntry otherTextEntry = new TextEntry("More claws", Optional.empty());
        userSpecWeaponToText.put(SpecWeapon.DRAGON_CLAWS, otherTextEntry);
        injectResolver(SpecWeaponTextPreset.NONE);

        for (int i = 0; i < 20; i++) {
            Optional<TextEntry> result = resolver.resolve(SpecWeapon.DRAGON_CLAWS);

            assertThat(result).isPresent();
            assertThat(result.get()).isIn(otherTextEntry, USER_DRAGON_CLAWS_TEXT);
        }
    }

    @Test
    public void clear_removesAllEntries() {
        resolver.clear();

        assertThat(resolver.resolve(SpecWeapon.GRANITE_MAUL)).isEmpty();
        assertThat(resolver.resolve(SpecWeapon.DRAGON_CLAWS)).isEmpty();
    }

    @Test
    public void clear_resetsPresetToNone() {
        injectResolver(SpecWeaponTextPreset.ANIME_MIX);

        resolver.clear();

        assertThat(resolver.resolve(SpecWeapon.ABYSSAL_WHIP)).isEmpty();
    }

    @Test
    public void reloadPresets_noPresetChange_nothing() {
        when(config.specialAttackPreset()).thenReturn(SpecWeaponTextPreset.NONE);

        resolver.reloadPresets();

        verify(mockPresetLoader, never()).loadPreset(any(SpecWeaponTextPreset.class));
    }

    @Test
    public void reloadPresets_presetsReturnsEmpty_emptyMaps() {
        when(config.specialAttackPreset()).thenReturn(SpecWeaponTextPreset.ANIME_MIX);
        when(mockPresetLoader.loadPreset(any(SpecWeaponTextPreset.class))).thenReturn(Optional.empty());

        assertThat(resolver.resolve(SpecWeapon.GRANITE_MAUL)).isPresent();

        resolver.reloadPresets();

        assertThat(resolver.resolve(SpecWeapon.GRANITE_MAUL)).isEmpty();
    }

    @Test
    public void reloadPresets_updateFromPresets() {
        when(config.specialAttackPreset()).thenReturn(SpecWeaponTextPreset.ANIME_MIX);
        when(mockPresetLoader.loadPreset(any(SpecWeaponTextPreset.class))).thenReturn(Optional.of(
                new ChantData("Frieren",
                        "",
                        "",
                        ImmutableListMultimap.of(),
                        ImmutableListMultimap.of(),
                        ImmutableListMultimap.of(SpecWeapon.GRANITE_MAUL, DRAGON_DAGGER_TEXT)
                )
        ));

        assertThat(resolver.resolve(SpecWeapon.GRANITE_MAUL)).hasValue(GRANITE_MAUL_TEXT);

        resolver.reloadPresets();

        assertThat(resolver.resolve(SpecWeapon.GRANITE_MAUL)).hasValue(DRAGON_DAGGER_TEXT);
    }

    @Test
    public void reloadOverrides_emptyOverrides_clearsUserEntries() {
        when(config.customSpecialAttackOverrides()).thenReturn("");

        assertThat(resolver.resolve(SpecWeapon.DRAGON_CLAWS)).hasValue(USER_DRAGON_CLAWS_TEXT);

        resolver.reloadOverrides();

        assertThat(resolver.resolve(SpecWeapon.DRAGON_CLAWS)).isEmpty();
    }
}