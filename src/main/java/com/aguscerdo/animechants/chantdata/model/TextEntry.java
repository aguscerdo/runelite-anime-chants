package com.aguscerdo.animechants.chantdata.model;

import com.google.gson.*;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

import java.lang.reflect.Type;
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

    /**
     * Accepts a bare string (text only) or an object with "text" and/or "sound".
     */
    public static class Deserializer implements JsonDeserializer<TextEntry> {
        private static Optional<String> optionalField(JsonObject obj, String field) {
            JsonElement el = obj.get(field);
            return el == null || el.isJsonNull()
                    ? Optional.empty()
                    : Optional.of(el.getAsString()).filter(s -> !s.isBlank());
        }

        @Override
        public TextEntry deserialize(JsonElement json, Type type, JsonDeserializationContext ctx)
                throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return new TextEntry(json.getAsString());
            }
            if (!json.isJsonObject()) {
                throw new JsonParseException(
                        "Preset entry must be a string or an object with 'text' and/or 'sound'");
            }

            JsonObject obj = json.getAsJsonObject();
            Optional<String> text = optionalField(obj, "text");
            Optional<String> sound = optionalField(obj, "sound");

            if (text.isEmpty() && sound.isEmpty()) {
                throw new JsonParseException("Preset entry needs at least one of 'text' or 'sound'");
            }
            return new TextEntry(text, sound);
        }
    }
}