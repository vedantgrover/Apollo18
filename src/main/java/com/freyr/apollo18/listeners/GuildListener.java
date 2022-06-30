package com.freyr.apollo18.listeners;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * This is an event listener. All events that are generic to the guild happen here.
 * <p>
 * Events are how Discord interacts with bots. Anything that happens in Discord is sent
 * to the bot as an event which we can use to do cool things like this.
 *
 * @author Freyr
 */
public class GuildListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannel().equals(event.getJDA().getChannelById(MessageChannel.class, 861700722482872371L))) {
            event.getChannel().getHistory().retrievePast(2).queue(messages -> {
                try {
                    int firstMessage = Integer.parseInt(messages.get(0).getContentRaw());
                    int secondMessage = Integer.parseInt(messages.get(1).getContentRaw());

                    if (firstMessage != secondMessage + 1 || messages.get(0).getAuthor().equals(messages.get(1).getAuthor())) {
                        messages.get(0).delete().queue();
                    }
                } catch (Exception e) {
                    event.getChannel().getHistory().retrievePast(1).queue(message -> message.get(0).delete().queue());
                }
            });
        }
    }
}
