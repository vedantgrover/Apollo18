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

    private final Apollo18 bot;

    public GuildListener(Apollo18 bot) {
        this.bot = bot;
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Database db = bot.getDatabase();

        if (db.getWelcomeSystemToggle(event.getGuild().getIdLong())) {
            MessageChannel welcomeChannel = (event.getGuild().getChannelById(MessageChannel.class, db.getWelcomeChannel(event.getGuild().getIdLong())) == null) ? (MessageChannel) event.getGuild().getDefaultChannel() :event.getGuild().getChannelById(MessageChannel.class, db.getWelcomeChannel(event.getGuild().getIdLong()));
            String welcomeMessage = db.getWelcomeMessage(event.getGuild().getIdLong()).replace("[member]", event.getUser().getAsMention()).replace("[server]", event.getGuild().getName());

            EmbedBuilder embed = new EmbedBuilder(); // Allows us to create and set the properties of an embed
            embed.setColor(EmbedColor.DEFAULT_COLOR); // Sets the color of the embed to the default embed located in EmbedColor.
            embed.setTitle(event.getUser().getName() + " has joined " + event.getGuild().getName()); // Setting the title, or who joined the server
            embed.setDescription(welcomeMessage); // Setting the description. Asking them to check the rules channel
            embed.setThumbnail(event.getUser().getAvatarUrl()); // Getting the profile picture of the user and setting it as the thumbnail
            embed.setFooter("Member #" + event.getGuild().getMemberCount()); // Telling the server what the member count is.

            welcomeChannel.sendMessageEmbeds(embed.build()).queue();
            if (db.getMemberCountChannel(event.getGuild().getIdLong()) != 0) {
                VoiceChannel memberCountChannel = event.getGuild().getVoiceChannelById(db.getMemberCountChannel(event.getGuild().getIdLong()));
                if (memberCountChannel == null) {
                    return;
                }

                memberCountChannel.getManager().setName("Member Count: " + event.getGuild().getMemberCount()).queue();
            }
        }

        db.createUserData(event.getUser());
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        Database db = bot.getDatabase();

        if (db.getWelcomeSystemToggle(event.getGuild().getIdLong())) {
            MessageChannel leaveChannel = (event.getGuild().getChannelById(MessageChannel.class, db.getLeaveChannel(event.getGuild().getIdLong())) == null) ? (MessageChannel) event.getGuild().getDefaultChannel() :event.getGuild().getChannelById(MessageChannel.class, db.getLeaveChannel(event.getGuild().getIdLong()));
            String leaveMessage = db.getLeaveMessage(event.getGuild().getIdLong()).replace("[member]", event.getUser().getAsMention()).replace("[server]", event.getGuild().getName());

            leaveChannel.sendMessage(leaveMessage).queue();

            if (db.getMemberCountChannel(event.getGuild().getIdLong()) != 0) {
                VoiceChannel memberCountChannel = event.getGuild().getVoiceChannelById(db.getMemberCountChannel(event.getGuild().getIdLong()));
                if (memberCountChannel == null) {
                    return;
                }

                memberCountChannel.getManager().setName("Member Count: " + event.getGuild().getMemberCount()).queue();
            }
        }
    }

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
