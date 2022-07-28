package com.freyr.apollo18.listeners;

import com.freyr.apollo18.Apollo18;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * This is a bot Event Listener. Anytime something happens in a server, it is registered as an event for the bot.
 * All events related to the bot should be coded here.
 *
 * @author Freyr
 */
public class BotListener extends ListenerAdapter {

    private final Apollo18 bot; // This is used to access the database

    /**
     * This is a bot Event Listener. Anytime something happens in a server, it is registered as an event for the bot.
     * All events related to the bot should be coded here.
     *
     * @param bot Getting the bot here so that we have access to the database classes.
     */
    public BotListener(Apollo18 bot) {
        this.bot = bot;
    }

    /**
     * This event fires everytime the bot is invited to a new guild
     *
     * @param event Has all the details about the event
     */
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        bot.getDatabase().createGuildData(event.getGuild());

        System.out.println("Joined " + event.getGuild().getName() + ". Data creation successful");
    }

    /**
     * This event fires everytime a message is sent in a guild
     *
     * @param event Has all the details about the event
     */
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        bot.getDatabase().createUserData(event.getAuthor());
    }
}
