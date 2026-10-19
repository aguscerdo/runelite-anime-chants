package com.aguscerdo.animechant.chantdata;

import com.aguscerdo.animechant.chantdata.model.ChantData;
import com.aguscerdo.animechant.chantdata.model.TextEntry;
import com.aguscerdo.animechant.chantdata.model.ValidationIssue;
import com.aguscerdo.animechant.specials.model.SpecWeapon;
import com.aguscerdo.animechant.spells.model.Spell;
import com.aguscerdo.animechant.spells.model.SpellCategory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.*;
import java.util.function.Function;

import static com.aguscerdo.animechant.chantdata.model.ValidationIssue.ValidationIssueReason;
import static com.aguscerdo.animechant.tools.StringUtils.removeSuffix;

/**
 * Handles custom override parsing (spells, spell categories, spec weapons).
 * <p>
 * Syntax, one override per line:
 * key = text
 * keyFile = files.wav
 */
public class CustomOverrides {
    public static String NONE = "NONE";

    /**
     * Parses spell user overrides by spell name or spell category name.
     */
    public static OverridesResult parseSpellOverrides(String spellOverrides) {
        Map<Spell, List<TextEntry>> spells = new HashMap<>();
        Map<SpellCategory, List<TextEntry>> spellCategories = new HashMap<>();

        ImmutableList<ValidationIssue> issues = parseLines(spellOverrides, (key, content, isFile) -> {
            Spell matchedSpell = Spell.fromString(key);
            if (!matchedSpell.isUnknown()) {
                addEntry(spells, matchedSpell, content, isFile);
                return true;
            }

            SpellCategory matchedCategory = SpellCategory.fromString(key);
            if (!matchedCategory.isUnknown()) {
                addEntry(spellCategories, matchedCategory, content, isFile);
                return true;
            }

            return false;
        });

        if (spells.isEmpty() && spellCategories.isEmpty() && issues.isEmpty()) {
            return new OverridesResult();
        }

        return new OverridesResult(
                new ChantData(
                        /* version= */ 0.f,
                        /* name=*/ "custom",
                        /* spellSoundDirectory */ "",
                        /* specWeaponSoundDirectory= */"",
                        spellCategories, spells,
                        /* specWeapons= */ ImmutableMap.of()),
                issues);
    }

    /**
     * Parse spec weapon overrides. They can be a weapon name or an item id.
     */
    public static OverridesResult parseSpecWeaponOverrides(String specWeaponOverrides) {
        SingleKeyParse<SpecWeapon> result = parseSingleKeyOverrides(specWeaponOverrides, SpecWeapon::fromString, SpecWeapon::isUnknown);

        return new OverridesResult(
                new ChantData(
                        /* version= */ 0.f,
                        /* name=*/ "custom",
                        /* spellSoundDirectory */ "",
                        /* specWeaponSoundDirectory= */"",
                        ImmutableMap.of(),
                        ImmutableMap.of(),
                        result.entries),
                result.issues);
    }

    /**
     * Generic parser for use cases with a single key type and no categories.
     *
     * @param resolver maps the normalized (trimmed, lowercase, suffix-stripped) key to a K;
     *                 may return an "unknown" sentinel, detected by isUnknown
     */
    private static <K> SingleKeyParse<K> parseSingleKeyOverrides(
            String overrides,
            Function<String, K> resolver,
            java.util.function.Predicate<K> isUnknown) {
        Map<K, List<TextEntry>> entries = new HashMap<>();

        ImmutableList<ValidationIssue> issues = parseLines(overrides, (key, content, isFile) -> {
            K matched = resolver.apply(key);
            if (matched == null || isUnknown.test(matched)) {
                return false;
            }
            addEntry(entries, matched, content, isFile);
            return true;
        });

        return new SingleKeyParse<>(entries, issues);
    }

    /**
     * Splits and validates lines, then hands each valid (key, content, isFile) to the router.
     * Returns the validation issues encountered.
     */
    private static ImmutableList<ValidationIssue> parseLines(String overrides, EntryRouter router) {
        if (overrides == null || overrides.isBlank()) {
            return ImmutableList.of();
        }

        ImmutableList.Builder<ValidationIssue> invalidLines = ImmutableList.builder();

        for (String line : (Iterable<String>) overrides.lines()::iterator) {
            if (line.isBlank()) continue;
            
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

            if (!router.route(key, content, isFile)) {
                invalidLines.add(new ValidationIssue(line, ValidationIssueReason.KEY_DOESNT_MATCH_ANYTHING));
            }
        }

        return invalidLines.build();
    }

    private static <K> void addEntry(Map<K, List<TextEntry>> target, K key, String content, boolean isFile) {
        List<TextEntry> existingEntries = target.computeIfAbsent(key, unused -> new LinkedList<>());

        if (!maybeMergeTextEntries(content, isFile, existingEntries)) {
            existingEntries.add(
                    new TextEntry(
                            /*text=*/ !isFile ? content : "",
                            /*files=*/ isFile ? Optional.of(content) : Optional.empty()));
        }
    }

    /**
     * Routes a normalized key to its destination. Returns false if the key matches nothing.
     */
    @FunctionalInterface
    private interface EntryRouter {
        boolean route(String key, String content, boolean isFile);
    }

    @Value
    @AllArgsConstructor
    public static class OverridesResult {
        ChantData chantData;
        ImmutableList<ValidationIssue> validationIssues;

        OverridesResult() {
            chantData = new ChantData();
            validationIssues = ImmutableList.of();
        }
    }

    /**
     * Attempts to merge textentries to match text and files files in order.
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
                it.set(entry.withText(Optional.ofNullable(content)));
                return true;
            }
        }

        return false;
    }


    /**
     * Intermediate result of the generic single-key parse.
     */
    @Value
    @AllArgsConstructor
    private static class SingleKeyParse<K> {
        Map<K, List<TextEntry>> entries;
        ImmutableList<ValidationIssue> issues;
    }
}