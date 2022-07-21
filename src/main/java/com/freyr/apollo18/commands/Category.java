package com.freyr.apollo18.commands;

import net.dv8tion.jda.api.entities.emoji.Emoji;

/**
 * This enum stores all the categories of commands. I use this in {@link com.freyr.apollo18.commands.utility.HelpCommand}
 * to get specific commands.
 *
 * @author Freyr
 */
public enum Category {

    UTILITY("Utility", ":tools:"), MUSIC("Music", ":musical_note:"), INFORMATION("Information", ":thinking:"), FUN("Fun", ":balloon:"), ECONOMY("Economy", "<:byte:858172448900644874>"), SETTINGS("Settings", ":gear:");

    public final String name; // Name of the Category
    public final String emoji; // What emoji to use with it

    Category(String name, String emoji) {
        this.name = name;
        this.emoji = emoji;
    }
}
