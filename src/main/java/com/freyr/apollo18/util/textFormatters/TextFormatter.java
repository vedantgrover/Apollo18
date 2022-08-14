package com.freyr.apollo18.util.textFormatters;

public class TextFormatter {

    public static String capitalize(String str) {
        String[] words = str.split(" ");
        String result = "";
        for (String word : words) {
            result += word.substring(0, 1).toUpperCase() + word.substring(1);
        }

        return result;
    }
}
