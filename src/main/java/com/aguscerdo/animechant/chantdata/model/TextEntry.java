package com.aguscerdo.animechant.chantdata.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

import java.io.IOException;
import java.util.Optional;

/**
 * One chant for a spell or category: the text, plus an optional files file.
 * Sound paths are relative to the chantdata resource directory.
 * <p>
 * YAML accepts either a bare string (text only) or an object:
 * - "Burn bright"
 * - text: "Burn bright"
 * files: fire/burn_bright.ogg
 */
@Value
@With
@JsonDeserialize(using = TextEntry.Deserializer.class)
public class TextEntry {

    Optional<String> text;

    @NonNull
    Optional<String> sound;

    public TextEntry(String text) {
        this.text = Optional.ofNullable(text);
        this.sound = Optional.empty();
    }


    public TextEntry(String text, Optional<String> sound) {
        this.text = Optional.ofNullable(text);
        this.sound = sound;
    }

    public TextEntry(String text, String sound) {
        this.text = Optional.ofNullable(text);
        this.sound = Optional.ofNullable(sound);
    }

    public TextEntry(Optional<String> text, Optional<String> sound) {
        this.text = text;
        this.sound = sound;
    }

    static class Deserializer extends StdDeserializer<TextEntry> {
        Deserializer() {
            super(TextEntry.class);
        }

        private static Optional<String> optionalField(JsonNode node, String field) {
            return node.hasNonNull(field)
                    ? Optional.of(node.get(field).asText()).filter(s -> !s.isBlank())
                    : Optional.empty();
        }

        @Override
        public TextEntry deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);

            if (node.isTextual()) {
                return new TextEntry(node.asText());
            }

            if (!node.isObject()) {
                throw JsonMappingException.from(p,
                        "Preset entry must be a string or an object with 'text' and/or 'sound'");
            }

            Optional<String> text = optionalField(node, "text");
            Optional<String> sound = optionalField(node, "sound");

            if (text.isEmpty() && sound.isEmpty()) {
                throw JsonMappingException.from(p,
                        "Preset entry needs at least one of 'text' or 'sound'");
            }

            return new TextEntry(text, sound);
        }
    }
}