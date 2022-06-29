package com.freyr.apollo18.listeners;

import com.freyr.apollo18.util.embeds.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * This is an event listener. All events that are generic to the guild happen here.
 * <p>
 * Events are how Discord interacts with bots. Anything that happens in Discord is sent
 * to the bot as an event which we can use to do cool things like this.
 *
 * @author Freyr
 */
public class GuildListener extends ListenerAdapter {

    /**
     * This method fires every time a user joins a server
     *
     * @param event - Has all the information on the event
     */
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        MessageChannel channel = event.getGuild().getChannelById(MessageChannel.class, 988658481932419142L); // Getting the channel through the ID
        VoiceChannel counterChannel = event.getGuild().getVoiceChannelById(988943638476226620L); // Getting the member count channel

        assert counterChannel != null; // Making sure that the channel exists
        counterChannel.getManager().setName("Member Count: " + event.getGuild().getMemberCount()).queue(); // Editing the channel name

        EmbedBuilder embed = new EmbedBuilder(); // Allows us to create and set the properties of an embed
        embed.setColor(EmbedColor.DEFAULT_COLOR); // Sets the color of the embed to the default embed located in EmbedColor.
        embed.setTitle(event.getUser().getName() + " has joined JHS CSHS!"); // Setting the title, or who joined the server
        embed.setDescription("Remember to check " + Objects.requireNonNull(event.getGuild().getRulesChannel()).getAsMention() + "  for full access to the server!"); // Setting the description. Asking them to check the rules channel
        embed.setThumbnail(event.getUser().getAvatarUrl()); // Getting the profile picture of the user and setting it as the thumbnail
        embed.setFooter("Member #" + event.getGuild().getMemberCount()); // Telling the server what the member count is.

        assert channel != null; // Making sure that the channel is not null
        channel.sendMessageEmbeds(embed.build()).queue(); // Sending the welcome message
    }

    /**
     * This method fires every time a user leaves a server (can include getting kicked or banned)
     *
     * @param event - Has all the information on the event
     */
    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        MessageChannel channel = event.getGuild().getChannelById(MessageChannel.class, 988945658532728862L); // Getting the leave log channel
        VoiceChannel counterChannel = event.getGuild().getVoiceChannelById(988943638476226620L); // Getting the member count voice channel

        assert counterChannel != null; // Making sure that the channel exists
        counterChannel.getManager().setName("Member Count: " + event.getGuild().getMemberCount()).queue(); // Editing the channel name

        EmbedBuilder embed = new EmbedBuilder(); // Allows us to create and set the properties of an embed
        embed.setDescription("**" + event.getUser().getName() + "** has left the server."); // Bolds the name of the user and formats it as "<user> has left the server."
        embed.setColor(EmbedColor.ERROR_COLOR); // Sets the color of the embed to the ERROR_COLOR in EmbedColor

        assert channel != null; // Making sure that the leave log channel exists
        channel.sendMessageEmbeds(embed.build()).queue(); // Sending the embed
    }
}
