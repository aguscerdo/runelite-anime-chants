package com.animechant.presets;

import com.animechant.TextPreset;
import com.animechant.spells.Spell;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableListMultimap;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import static com.google.common.collect.ImmutableListMultimap.toImmutableListMultimap;

@Slf4j
@Singleton
public class PresetLoader {
    private static final Path PRESETS_DIR = Path.of("/presets");

    private static final ObjectMapper YAML_MAPPER = PresetYaml.createMapper();

    public Optional<RawPresetData> loadPreset(TextPreset preset) {
        if (preset == TextPreset.SPELL_NAME) {
            return Optional.of(makeSpellNamePreset());
        }

        if (preset.getFileName().isEmpty()) {
            return Optional.empty();
        }

        Path presetPath = PRESETS_DIR.resolve(preset.getFileName());
        try (InputStream in = PresetLoader.class.getResourceAsStream(presetPath.toString())) {
            if (in == null) {
                log.warn("Preset resource {} not found", presetPath);
                return Optional.empty();
            }

            return loadFromYaml(new InputStreamReader(in, StandardCharsets.UTF_8), presetPath);
        } catch (IOException e) {
            log.warn("Failed reading preset {}", presetPath, e);
            return Optional.empty();
        }
    }


    private static Optional<RawPresetData> loadFromYaml(Reader reader, Path source) {
        try {
            return Optional.ofNullable(YAML_MAPPER.readValue(reader, RawPresetData.class));
        } catch (IOException e) {
            log.warn("Invalid YAML in {}", source, e);
            return Optional.empty();
        }
    }


    private static RawPresetData makeSpellNamePreset() {
        ImmutableListMultimap<Spell, String> spells = Arrays.stream(Spell.values()).collect(
                toImmutableListMultimap(
                        Function.identity(),
                        Spell::getName
                )
        );

        return new RawPresetData(
                TextPreset.SPELL_NAME.name(),
                /* spellCategories= */ ImmutableListMultimap.of(),
                /* spells= */ spells
        );
    }
}