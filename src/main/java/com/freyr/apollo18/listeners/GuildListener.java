package com.freyr.apollo18.listeners;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.util.embeds.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
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

    private final Apollo18 bot; // This allows us to get access to the database

    /**
     * This is an event listener. All events that are generic to the guild happen here.
     * <p>
     * Events are how Discord interacts with bots. Anything that happens in Discord is sent
     * to the bot as an event which we can use to do cool things like this.
     *
     * @param bot We send in an instance of the bot so that we have access to the database
     * @author Freyr
     */
    public GuildListener(Apollo18 bot) {
        this.bot = bot;
    }

    /**
     * This event fires everytime a user enters a server
     *
     * @param event Has all the details about the event
     */
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Database db = bot.getDatabase(); // Grabbing the database

        if (db.getWelcomeSystemToggle(event.getGuild().getId())) { // Checking if the server has turned the welcoming system on
            MessageChannel welcomeChannel = (event.getGuild().getChannelById(MessageChannel.class, db.getWelcomeChannel(event.getGuild().getId())) == null) ? (MessageChannel) event.getGuild().getDefaultChannel() : event.getGuild().getChannelById(MessageChannel.class, db.getWelcomeChannel(event.getGuild().getId())); // Grabbing the default channel if the user has not set a server channel
            String welcomeMessage = db.getWelcomeMessage(event.getGuild().getId()).replace("[member]", event.getUser().getAsMention()).replace("[server]", event.getGuild().getName()); // Grabbing the welcome message from the database and replacing all of the placeholders with their respective values

            EmbedBuilder embed = new EmbedBuilder(); // Allows us to create and set the properties of an embed
            embed.setColor(EmbedColor.DEFAULT_COLOR); // Sets the color of the embed to the default embed located in EmbedColor.
            embed.setTitle(event.getUser().getName() + " has joined " + event.getGuild().getName()); // Setting the title, or who joined the server
            embed.setDescription(welcomeMessage); // Setting the description. Asking them to check the rules channel
            embed.setThumbnail(event.getUser().getAvatarUrl()); // Getting the profile picture of the user and setting it as the thumbnail
            embed.setFooter("Member #" + event.getGuild().getMemberCount()); // Telling the server what the member count is.

            welcomeChannel.sendMessageEmbeds(embed.build()).queue(); // Sending the message to the welcome channel
            if (db.getMemberCountChannel(event.getGuild().getId()) != null) { // Checking if the user has set a member count channel
                VoiceChannel memberCountChannel = event.getGuild().getVoiceChannelById(db.getMemberCountChannel(event.getGuild().getId())); // Grabbing the channel
                if (memberCountChannel == null) { // If that channel doesn't exist, it does nothing
                    return;
                }

                memberCountChannel.getManager().setName("Member Count: " + event.getGuild().getMemberCount()).queue(); // Editing the name of the channel to the current number of users
            }
        }

        db.createUserData(event.getUser()); // Creating the data for that user when they join
        db.createLevelingProfile(event.getUser().getId(), event.getGuild().getId());
        db.updateServerStatus(event.getUser().getId(), event.getGuild().getId(), true);
    }

    /**
     * This event fires everytime a member leaves/kicked/banned from a server
     *
     * @param event has all the details about the event
     */
    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        Database db = bot.getDatabase(); // Getting the database

        if (db.getWelcomeSystemToggle(event.getGuild().getId())) { // Checking to see if the server has the welcoming system turned on for their server
            MessageChannel leaveChannel = (event.getGuild().getChannelById(MessageChannel.class, db.getLeaveChannel(event.getGuild().getId())) == null) ? (MessageChannel) event.getGuild().getDefaultChannel() : event.getGuild().getChannelById(MessageChannel.class, db.getLeaveChannel(event.getGuild().getId())); // Grabbing the default channel if the user hasn't set a leave channel
            String leaveMessage = db.getLeaveMessage(event.getGuild().getId()).replace("[member]", event.getUser().getAsMention()).replace("[server]", event.getGuild().getName()); // Grabbing the leave message and replacing all placeholders

            leaveChannel.sendMessage(leaveMessage).queue(); // Sending the message to the leave channel

            if (db.getMemberCountChannel(event.getGuild().getId()) != null) { // Checking to see if the user has set a member count channel
                VoiceChannel memberCountChannel = event.getGuild().getVoiceChannelById(db.getMemberCountChannel(event.getGuild().getId())); // Grabbing the member count channel
                if (memberCountChannel == null) { // If the channel doesn't exist, it does stops and does nothing.
                    return;
                }

                memberCountChannel.getManager().setName("Member Count: " + event.getGuild().getMemberCount()).queue(); // Editing the name of the channel and setting it to the new member count
            }
        }

        db.updateServerStatus(event.getUser().getId(), event.getGuild().getId(), false);
    }

    /**
     * This method fires everytime a message is sent
     *
     * @param event Has all the information about the bot
     */
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannel().equals(event.getJDA().getChannelById(MessageChannel.class, 861700722482872371L))) { // Grabbing the count channel within Delphi's Cave
            event.getChannel().getHistory().retrievePast(2).queue(messages -> { // Grabbing the past two messages
                try {
                    int firstMessage = Integer.parseInt(messages.get(0).getContentRaw()); // Converting the first message into an integer
                    int secondMessage = Integer.parseInt(messages.get(1).getContentRaw()); // Converting the second message into an integer

                    if (firstMessage != secondMessage + 1 || messages.get(0).getAuthor().equals(messages.get(1).getAuthor())) { // Comparing both messages to make sure that the new number is following the chain and that the author of the new number is not the same as the author of the previous number.
                        messages.get(0).delete().queue(); // Deleting the message if the conditions are true
                    }
                } catch (Exception e) {
                    event.getChannel().getHistory().retrievePast(1).queue(message -> message.get(0).delete().queue()); // If anything goes wrong in the previous message (parsing a non-number string), it will delete the new message.
                }
            });
        }
    }
}
