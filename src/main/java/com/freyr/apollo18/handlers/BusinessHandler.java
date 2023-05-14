package com.freyr.apollo18.handlers;

public class BusinessHandler {

    private static final String upArrow = "<:increase:1107148093830471681>";
    private static final String downArrow = "<:decrease:1107148133496004748>";
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
