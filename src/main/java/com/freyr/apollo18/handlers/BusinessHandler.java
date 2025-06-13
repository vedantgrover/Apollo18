package com.freyr.apollo18.handlers;

public class BusinessHandler {

    public static final String upArrow = "<:increase:1378007802404012063>";
    public static final String downArrow = "<:decrease:1378007776546127882>";
    public static final String neutral = "<:same:1378007830648324176>";
    public static final String byteEmoji = "<:byte:1378007816861782255>";

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
