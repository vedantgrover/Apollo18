package com.freyr.apollo18.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a basic command layout. It allows us to create commands very easily. The command manager then can use
 * this data to add commands into Discord!
 *
 * @author Freyr
 */
public abstract class Command {

    public String name; // Name of the command
    public String description; // Description of the command
    public Category category;
    public List<OptionData> args; // Any options the command needs goes here

    /**
     * Command Constructor.
     * Initializes args as an empty arraylist
     */
    public Command() {
        this.args = new ArrayList<>();
    }

    /**
     * This method will contain the code that we want the bot to execute when that command is called.
     *
     * @param event Has all the information about the event.
     */
    public abstract void execute(SlashCommandInteractionEvent event);
}
