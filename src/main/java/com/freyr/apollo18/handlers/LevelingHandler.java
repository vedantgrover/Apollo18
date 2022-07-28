package com.freyr.apollo18.handlers;

/**
 * Handles all the leveling bits
 *
 * @author Freyr
 */
public class LevelingHandler {

    /**
     * This method calculates the number of xp needed for the user to proceed to the next level.
     *
     * @param level The current user level
     * @return The number of xp needed to advance to the next level
     */
    public static int calculateLevelGoal(int level) {
        return (int) (5 * Math.pow(level, 2) + 50 * level + 100);
    }

    /**
     * This method generates a random number of bytes to give to the user
     * @return
     */
    public static int randomNumBytes() {
        return (int) (Math.random() * ((3 - 1) + 1)) + 1;
    }
}
