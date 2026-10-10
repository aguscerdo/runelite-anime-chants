package com.aguscerdo.animechant.chantdata;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ValidationIssue {
    String text;
    ValidationIssueReason issueReason;
    @AllArgsConstructor
    public enum ValidationIssueReason {
        LINE_NOT_SEPARATED_BY_EQUALS("Each line must be separated by an equals `=`."),
        FILES_MUST_END_IN_WAV("Audio files must be of type `.wav`"),
        KEY_DOESNT_MATCH_ANY_SPELL("Key doesn't match any spell or spell category."),
        CONTENT_TOO_LONG("Content exceeds 100 characters");

        private final String description;
    }
}
