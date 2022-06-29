package com.freyr.apollo18.commands;

/**
 * This enum stores all the categories of commands. I use this in {@link com.freyr.apollo18.commands.utility.HelpCommand}
 * to get specific commands.
 *
 * @author Freyr
 */
public enum Category {

    UTILITY("Utility", ":tools:"), MUSIC("Music", ":musical_note:");

    public String name; // Name of the Category
    public String emoji; // What emoji to use with it

    Category(String name, String emoji) {
        this.name = name;
        this.emoji = emoji;
    }
}
