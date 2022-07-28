package com.freyr.apollo18.listeners;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.util.embeds.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * This is a generic Event Listener. Anytime something happens in a server, it is registered as an event for the bot.
 * All Generic Events (Non-specific events) should be coded in here.
 *
 * @author Freyr
 */
public class BotListener extends ListenerAdapter {

    private final Apollo18 bot;

    public BotListener(Apollo18 bot) {
        this.bot = bot;
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        bot.getDatabase().createGuildData(event.getGuild());

        System.out.println("Joined " + event.getGuild().getName() + ". Data creation successful");
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        bot.getDatabase().createUserData(event.getAuthor());
    }
}
