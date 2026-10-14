package com.aguscerdo.animechant.specials;

import com.aguscerdo.animechant.AnimeChantConfig;
import com.aguscerdo.animechant.chantdata.CustomOverrides;
import com.aguscerdo.animechant.chantdata.PresetLoader;
import com.aguscerdo.animechant.chantdata.model.ChantData;
import com.aguscerdo.animechant.chantdata.model.TextEntry;
import com.aguscerdo.animechant.chantdata.model.ValidationIssue;
import com.aguscerdo.animechant.specials.model.SpecWeapon;
import com.aguscerdo.animechant.specials.model.SpecWeaponTextPreset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;

/**
 * One layer of text: spell-name entries and category entries. Each entry holds one or
 * more variants; one is picked at random on lookup. Also applies the display throttles.
 */
@Slf4j
@Singleton
public class SpecialAttackTextResolverImpl implements SpecialAttackTextResolver {
    @Inject
    private Client client;

    @Inject
    private PresetLoader presetLoader;

    @Inject
    private AnimeChantConfig config;

    @Inject
    ScheduledExecutorService executor;

    private SpecWeaponTextPreset currentPreset;

    private ImmutableListMultimap<SpecWeapon, TextEntry> presetSpecWeaponToText;
    private ImmutableListMultimap<SpecWeapon, TextEntry> userSpecWeaponToText;


    private ImmutableList<ValidationIssue> overrideValidationIssues;

    public SpecialAttackTextResolverImpl() {
        currentPreset = SpecWeaponTextPreset.NONE;
        presetSpecWeaponToText = ImmutableListMultimap.of();
    }

    SpecialAttackTextResolverImpl(SpecWeaponTextPreset currentPreset,
                                  ListMultimap<SpecWeapon, TextEntry> presetSpecWeaponToText,
                                  ListMultimap<SpecWeapon, TextEntry> userSpecWeaponToText

    ) {
        this.currentPreset = currentPreset;

        this.presetSpecWeaponToText = ImmutableListMultimap.copyOf(presetSpecWeaponToText);
        this.userSpecWeaponToText = ImmutableListMultimap.copyOf(userSpecWeaponToText);
    }

    /**
     * Chooses a random entry from a list
     */
    private static TextEntry randomChoice(List<TextEntry> variants) {
        return variants.size() == 1 ? variants.get(0)
                : variants.get(ThreadLocalRandom.current().nextInt(variants.size()));
    }

    @Override
    public void clear() {
        currentPreset = SpecWeaponTextPreset.NONE;

        presetSpecWeaponToText = ImmutableListMultimap.of();
        userSpecWeaponToText = ImmutableListMultimap.of();

        overrideValidationIssues = ImmutableList.of();

    }

    @Override
    public void reloadAll() {
        reloadPresets();
        reloadOverrides();
    }

    @Override
    public void reloadPresets() {
        if (currentPreset == config.specialAttackPreset()) {
            return;
        }

        executor.execute(() -> {
            currentPreset = config.specialAttackPreset();
            Optional<ChantData> presetData = presetLoader.loadPreset(currentPreset);

            if (presetData.isPresent()) {
                presetSpecWeaponToText = ImmutableListMultimap.copyOf(presetData.get().getSpecWeapons());
            } else {
                presetSpecWeaponToText = ImmutableListMultimap.of();
            }
        });
    }

    @Override
    public void reloadOverrides() {
        CustomOverrides.OverridesResult result = CustomOverrides.parseSpecWeaponOverrides(config.customSpecialAttackOverrides());

        overrideValidationIssues = result.getValidationIssues();
        userSpecWeaponToText = result.getChantData().getSpecWeapons();
    }

    @Override
    public Optional<TextEntry> resolve(SpecWeapon specWeapon) {
        if (specWeapon.isUnknown()) {
            return Optional.empty();
        }

        // User Overrides
        List<TextEntry> variants = userSpecWeaponToText.get(specWeapon);
        if (variants != null && !variants.isEmpty()) {
            log.debug("Resolved spec weapon to user override: {}}", specWeapon);
            return Optional.of(randomChoice(variants));
        }

        variants = presetSpecWeaponToText.get(specWeapon);
        if (variants != null && !variants.isEmpty()) {
            log.debug("Resolved spec weapon to preset: {}", specWeapon);
            return Optional.of(randomChoice(variants));
        }

        switch (config.specialAttackFallback()) {
            case SPECIAL_ATTACK_NAME:
                return Optional.of(specWeapon.specialAttackNameAsTextEntry());
            case WEAPON_NAME:
                return Optional.of(specWeapon.nameAsTextEntry());

        }

        return Optional.empty();
    }
}
