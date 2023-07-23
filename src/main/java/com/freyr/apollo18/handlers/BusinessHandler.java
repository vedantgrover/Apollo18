package com.freyr.apollo18.handlers;

public class BusinessHandler {

    public static final String upArrow = "<:increase:1107148093830471681>";
    public static final String downArrow = "<:decrease:1107148133496004748>";
    public static final String neutral = "<:no_change:1107057693627269171>";
    public static final String byteEmoji = "<:byte:1109318930901766224>";

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
