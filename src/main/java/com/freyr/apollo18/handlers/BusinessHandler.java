package com.freyr.apollo18.handlers;

public class BusinessHandler {

    public static final String upArrow = "<:increase:1377821574509367396>";
    public static final String downArrow = "<:decrease:1377821567995744266>";
    public static final String neutral = "<:no_change:1377822916347170956>";
    public static final String byteEmoji = "<:byte:1377825496003379330>";

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
