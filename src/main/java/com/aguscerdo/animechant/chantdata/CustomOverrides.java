package com.aguscerdo.animechant.chantdata;

import com.aguscerdo.animechant.spells.Spell;
import com.aguscerdo.animechant.spells.SpellCategory;
import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.*;

import static com.aguscerdo.animechant.chantdata.ValidationIssue.ValidationIssueReason;

/**
 * Handles custom spell override
 */
public class CustomOverrides {
    public static String NONE = "NONE";

    public static CustomOverridesResult parseSpellOverrides(String spellOverrides) {
        if (spellOverrides == null || spellOverrides.isBlank()) {
            return new CustomOverridesResult();
        }

        ImmutableList.Builder<ValidationIssue> invalidLines = ImmutableList.builder();

        Map<Spell, List<TextEntry>> spells = new HashMap<>();
        Map<SpellCategory, List<TextEntry>> spellCategories = new HashMap<>();

        for (String line : (Iterable<String>) spellOverrides.lines()::iterator) {
            String[] lineParts = line.split("=", 2);
            if (lineParts.length != 2) {
                invalidLines.add(new ValidationIssue(line, ValidationIssueReason.LINE_NOT_SEPARATED_BY_EQUALS));
                continue;
            }

            String key = lineParts[0].trim().toLowerCase(Locale.ROOT);
            String content = lineParts[1].trim();

            if (content.length() > 100) {
                invalidLines.add(
                        new ValidationIssue(line.substring(0, 97) + "...",
                                ValidationIssueReason.CONTENT_TOO_LONG));
                continue;
            }
            if (content.isEmpty() || content.toUpperCase(Locale.ROOT).equals(NONE)) {
                content = NONE;
            }

            boolean isFile = false;

            // Audio File
            if (key.endsWith("file")) {
                if (!content.endsWith(".wav") && !content.equals(NONE)) {
                    invalidLines.add(new ValidationIssue(line, ValidationIssueReason.FILES_MUST_END_IN_WAV));
                    continue;
                }
                key = removeSuffix(key, "file");
                isFile = true;
            }

            Spell matchedSpell = Spell.fromString(key);
            if (!matchedSpell.isUnknown()) {
                List<TextEntry> existingEntries = spells.computeIfAbsent(matchedSpell, unused -> new LinkedList<>());

                if (!maybeMergeTextEntries(content, isFile, existingEntries)) {
                    existingEntries.add(
                            new TextEntry(
                                    /*text=*/ !isFile ? content : "",
                                    /*sound=*/ isFile ? Optional.of(content) : Optional.empty()));
                }

                continue;
            }

            SpellCategory matchedCategory = SpellCategory.fromString(key);
            if (!matchedCategory.isUnknown()) {
                List<TextEntry> existingEntries = spellCategories.computeIfAbsent(matchedCategory, unused -> new LinkedList<>());

                if (!maybeMergeTextEntries(content, isFile, existingEntries)) {
                    existingEntries.add(
                            new TextEntry(
                                    /*text=*/ !isFile ? content : "",
                                    /*sound=*/ isFile ? Optional.of(content) : Optional.empty()));
                }

                continue;
            }

            invalidLines.add(new ValidationIssue(line, ValidationIssueReason.KEY_DOESNT_MATCH_ANY_SPELL));
        }

        return new CustomOverridesResult(
                new ChantData("custom", spellCategories, spells),
                invalidLines.build()
        );
    }

    /**
     * Attempts to merge textentries to match text and sound files in order.
     */
    private static boolean maybeMergeTextEntries(String content, boolean isFile, List<TextEntry> entries) {
        if (entries.isEmpty()) {
            return false;
        }

        ListIterator<TextEntry> it = entries.listIterator();
        while (it.hasNext()) {
            TextEntry entry = it.next();

            if (isFile && entry.getSound().isEmpty()) {
                it.set(entry.withSound(Optional.of(content)));
                return true;
            }
            if (!isFile && entry.getText().isEmpty()) {
                it.set(entry.withText(content));
                return true;
            }
        }

        return false;
    }

    private static String removeSuffix(String s, String suffix) {
        return s.endsWith(suffix)
                ? s.substring(0, s.length() - suffix.length()).trim()
                : s;
    }

    @Value
    @AllArgsConstructor
    public static class CustomOverridesResult {
        ChantData chantData;
        ImmutableList<ValidationIssue> validationIssues;

        CustomOverridesResult() {
            chantData = new ChantData();
            validationIssues = ImmutableList.of();
        }
    }
}
