package com.freyr.apollo18.handlers;

/**
 * Handles all the leveling bits
 *
 * @author Freyr
 */
public class LevelingHandler {

    public static int calculateLevelGoal(int level) {
        return (int) (5 * Math.pow(level, 2) + 50 * level + 100);
    }

    public static int randomNumBytes() {
        return (int) (Math.random() * ((10 - 5) + 1)) + 5;
    }
}
