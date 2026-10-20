package com.aguscerdo.animechants.tools;

public class StringUtils {
    public static String removeSuffix(String s, String suffix) {
        return s.endsWith(suffix)
                ? s.substring(0, s.length() - suffix.length()).trim()
                : s;
    }
}
