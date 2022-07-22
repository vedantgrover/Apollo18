package com.freyr.apollo18.listeners;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.handlers.LevelingHandler;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

public class LevelingListener extends ListenerAdapter {

    private final Apollo18 bot;

    public LevelingListener(Apollo18 bot) {
        this.bot = bot;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Database db = bot.getDatabase();
        if (db.getLevelingSystemToggle(event.getGuild().getId())) {
            if (event.getAuthor().isBot()) return;
            if (!event.getChannelType().isGuild()) return;

            if (db.getUserLevelingProfile(event.getAuthor().getId(), event.getGuild().getId()) == null) db.createLevelingProfile(event.getAuthor().getId(), event.getGuild().getId());
            MessageChannel channel = (db.getLevelingChannel(event.getGuild().getId()) == null) ? event.getChannel() : event.getGuild().getChannelById(MessageChannel.class, db.getLevelingChannel(event.getGuild().getId()));
            Document userDoc = db.getUserLevelingProfile(event.getAuthor().getId(), event.getGuild().getId());

            db.addXptoUser(event.getAuthor().getId(), event.getGuild().getId());
            int maxXp = LevelingHandler.calculateLevelGoal(userDoc.getInteger("level"));

            if (userDoc.getInteger("xp") >= maxXp) {
                db.levelUp(event.getAuthor().getId(), event.getGuild().getId());
                channel.sendMessage(db.getLevelingMessage(event.getGuild().getId()).replace("[member]", event.getAuthor().getAsMention()).replace("[level]", String.valueOf(userDoc.getInteger("level") + 1)).replace("[server]", event.getGuild().getName())).queue();
            }
        }
    }
}
