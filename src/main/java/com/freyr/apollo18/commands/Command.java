package com.freyr.apollo18.commands;

import com.freyr.apollo18.Apollo18;
import net.dv8tion.jda.api.Permission;
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

    public Apollo18 bot; // This gives us access to the config file which is in the main class
    public String name; // Name of the command
    public String description; // Description of the command
    public Category category;
    public List<OptionData> args; // Any options the command needs goes here
    public List<Permission> userPermission; // Permissions for the user
    public List<Permission> botPermission; // Permissions the bot needs

    /**
     * Command Constructor.
     * Initializes args as an empty arraylist
     */
    public Command(Apollo18 bot) {
        this.bot = bot;
        this.args = new ArrayList<>();
        this.botPermission = new ArrayList<>();
        this.userPermission = new ArrayList<>();
    }

    /**
     * This method will contain the code that we want the bot to execute when that command is called.
     *
     * @param event Has all the information about the event.
     */
    public abstract void execute(SlashCommandInteractionEvent event);
}
