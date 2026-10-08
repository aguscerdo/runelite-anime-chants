package com.animechant.presets;

import com.animechant.enums.Spell;
import com.animechant.enums.SpellCategory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.util.function.Function;

final class PresetYaml {

    private PresetYaml() {
    }

    static ObjectMapper createMapper() {
        SimpleModule module = new SimpleModule("PresetEnums");
        registerEnum(module, Spell.class, Spell::fromString);
        registerEnum(module, SpellCategory.class, SpellCategory::fromString);

        return new ObjectMapper(new YAMLFactory())
                .registerModule(module)
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

    private static <E> void registerEnum(SimpleModule module, Class<E> type, Function<String, E> parser) {
        module.addDeserializer(type, new StdDeserializer<E>(type) {
            @Override
            public E deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return parser.apply(p.getValueAsString());
            }
        });

        module.addKeyDeserializer(type, new KeyDeserializer() {
            @Override
            public Object deserializeKey(String key, DeserializationContext ctxt) {
                return parser.apply(key);
            }
        });
    }
}