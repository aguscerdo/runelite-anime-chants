package com.aguscerdo.animechant.presets;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.Value;
import lombok.With;

import java.io.IOException;
import java.util.Optional;

/**
 * One chant for a spell or category: the text, plus an optional sound file.
 * Sound paths are relative to the presets resource directory.
 * <p>
 * YAML accepts either a bare string (text only) or an object:
 * - "Burn bright"
 * - text: "Burn bright"
 * sound: fire/burn_bright.ogg
 */
@Value
@With
@JsonDeserialize(using = TextEntry.Deserializer.class)
public class TextEntry {

    String text;

    Optional<String> sound;

    static class Deserializer extends StdDeserializer<TextEntry> {
        Deserializer() {
            super(TextEntry.class);
        }

        @Override
        public TextEntry deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);

            if (node.isTextual()) {
                return new TextEntry(node.asText(), null);
            }

            if (!node.isObject() || !node.hasNonNull("text")) {
                throw JsonMappingException.from(p, "Preset entry must be a string or an object with 'text'");
            }

            Optional<String> sound = node.hasNonNull("sound")
                    ? Optional.of(node.get("sound").asText())
                    : Optional.empty();
            if (sound.isPresent() && sound.get().isBlank()) {
                sound = Optional.empty();
            }

            return new TextEntry(node.get("text").asText(), sound);
        }
    }
}