package com.animechant.presets;

import com.animechant.ChantResolver;
import com.animechant.enums.Spell;
import com.animechant.enums.SpellCategory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.Reader;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@Slf4j
public class RawPresetData {
    // YAML Fields.
    private String name;
    private Map<SpellCategory, List<String>> spellCategories = new LinkedHashMap<>();
    private Map<Spell, List<String>> spells = new LinkedHashMap<>();

}