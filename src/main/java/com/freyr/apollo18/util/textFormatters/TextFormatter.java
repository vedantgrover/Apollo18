package com.freyr.apollo18.util.textFormatters;

public class TextFormatter {

    public static String capitalize(String str) {
        String firstLetter = str.substring(0, 1).toUpperCase();
        String restOfWord = str.substring(1);

        return firstLetter + restOfWord;
    }
}
