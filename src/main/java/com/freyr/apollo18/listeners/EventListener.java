package com.freyr.apollo18.listeners;

import com.freyr.apollo18.util.embeds.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
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
public class EventListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Message msg = event.getMessage(); // Getting the message from the event. (The Command)

        // Base prefix
        final String PREFIX = "a.";
        if (msg.getContentRaw().equals(PREFIX + "ping")) { // If the command is a.ping...
            long time = System.currentTimeMillis(); // Getting the current system time
            EmbedBuilder embed = new EmbedBuilder(); // Allows us to create and set the properties of an embed
            embed.setColor(new Color(EmbedColor.DEFAULT_COLOR)); // Setting the color to dark green
            embed.setDescription(":signal_strength: Calculating..."); // Setting the description of the embed.

            event.getChannel().sendMessageEmbeds(embed.build()).queue(m -> { // Sending the embed (built above) and then editing it
                long latency = System.currentTimeMillis() - time; // Getting the difference in time between sending the previous message and editing the message.
                EmbedBuilder latencyEmbed = new EmbedBuilder(); // Allows us to create and set the properties of an embed
                latencyEmbed.setTitle(":ping_pong: Pong!"); // Sets the title to "🏓 Pong!"
                latencyEmbed.setColor(new Color(EmbedColor.DEFAULT_COLOR)); // Sets the color to dark green
                latencyEmbed.addField("Bot Latency", latency + "ms", false); // Creates a field within the embed with a title and a description. We are displaying the latency here
                latencyEmbed.addField("Websocket", event.getJDA().getGatewayPing() + "ms", false); // Creates a field within the embed with a title and a description. We are getting and displaying the websocket latency here
                m.editMessageEmbeds(latencyEmbed.build()).queue(); // Editing the previously sent message to the new message.
            });
        }
    }
}
