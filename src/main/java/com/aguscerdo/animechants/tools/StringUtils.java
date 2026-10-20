package com.aguscerdo.animechants.tools;

public class StringUtils {
    public static String removeSuffix(String s, String suffix) {
        return s.endsWith(suffix)
                ? s.substring(0, s.length() - suffix.length()).trim()
                : s;
    }

    public static String joinPath(String dir, String file) {
        String d = dir.replace('\\', '/');
        String f = file.replace('\\', '/');

        if (d.endsWith("/")) d = d.substring(0, d.length() - 1);
        if (f.startsWith("/")) f = f.substring(1);

        return d + "/" + f;
    }
}
