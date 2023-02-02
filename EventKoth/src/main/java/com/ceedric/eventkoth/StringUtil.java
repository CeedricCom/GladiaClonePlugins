package com.ceedric.eventkoth;

import java.util.Locale;

public class StringUtil {

    public static String getFriendlyName(Enum<?> inum) {
        String name = inum.name().toLowerCase(Locale.ROOT);
        String str = name.replace('_',' ');
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String getFriendlyName(String string) {
        String name = string.toLowerCase(Locale.ROOT);
        String str = name.replace('_',' ');
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String getOrdinal(int num) {
        int lastDigit = num % 10;
        if (lastDigit == 1 && num != 11) {
            return num + "st";
        } else if (lastDigit == 2 && num != 12) {
            return num + "nd";
        } else if (lastDigit == 3 && num != 13) {
            return num + "rd";
        } else {
            return num + "th";
        }
    }

}
