package com.freyr.apollo18.handlers;

public class BusinessHandler {

    private static final String upArrow = "<:up_green_arrow:877101834257449010>";
    private static final String downArrow = "<:down_red_arrow:877102121038778368>";
    private static final String neutral = "<:no_change:1107057693627269171>";
    public static final String byteEmoji = "";

    public static String getArrow(int change) {
        if (change == 0) {
            return neutral;
        } else if (change > 0) {
            return upArrow;
        } else {
            return downArrow;
        }
    }

}
