package com.freyr.apollo18.listeners;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.handlers.LevelingHandler;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

/**
 * This is where all events related to the leveling system of the bot happen.
 *
 * @author Freyr
 */
public class LevelingListener extends ListenerAdapter {

    private final Apollo18 bot; // Gives us access to the database

    /**
     * This is where all events related to the leveling system of the bot happen.
     *
     * @author Freyr
     */
    public LevelingListener(Apollo18 bot) {
        this.bot = bot;
    }

    /**
     * This method fires everytime a new message is sent
     *
     * @param event Has all the details about the event
     */
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Database db = bot.getDatabase(); // Getting the database
        if (db.getLevelingSystemToggle(event.getGuild().getId())) { // Checking to see if the server has the leveling system turned on
            if (event.getAuthor().isBot()) return; // If the author of the message is a bot, then do stop and do nothing
            if (!event.getChannelType().isGuild()) return; // If the channel is not a guild channel, stop and do nothing

            if (db.getUserLevelingProfile(event.getAuthor().getId(), event.getGuild().getId()) == null)
                db.createLevelingProfile(event.getAuthor().getId(), event.getGuild().getId()); // If the user does not have any levels within this server, it creates a profile within the user data.
            MessageChannel channel = (db.getLevelingChannel(event.getGuild().getId()) == null) ? event.getChannel() : event.getGuild().getChannelById(MessageChannel.class, db.getLevelingChannel(event.getGuild().getId())); // Grabbing the leveling channel set by the user. If the channel does not exist, it grabs the channel the message was sent in.
            Document userDoc = db.getUserLevelingProfile(event.getAuthor().getId(), event.getGuild().getId()); // Grabbing the leveling data for the user

            db.addXptoUser(event.getAuthor().getId(), event.getGuild().getId()); // Adding the XP to a user. This can be from 10 to 15 xp points
            int maxXp = LevelingHandler.calculateLevelGoal(userDoc.getInteger("level")); // Calculating the number of xp the user needs to level up

            if (userDoc.getInteger("xp") >= maxXp) { // If the user's current xp exceeds that, then they level up
                int bytesAdded = LevelingHandler.randomNumBytes(); // The number of bytes that the user gets when they level up.
                db.levelUp(event.getAuthor().getId(), event.getGuild().getId(), bytesAdded); // Leveling the user up
                channel.sendMessage(db.getLevelingMessage(event.getGuild().getId()).replace("[member]", event.getAuthor().getAsMention()).replace("[level]", String.valueOf(userDoc.getInteger("level") + 1)).replace("[server]", event.getGuild().getName()).replace("[bytes]", "<:byte:858172448900644874> " + bytesAdded)).queue(); // Grabbing the leveling up message and replacing placeholders with their values
            }
        }
    }
}
