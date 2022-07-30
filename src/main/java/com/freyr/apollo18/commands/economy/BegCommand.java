package com.freyr.apollo18.commands.economy;

import com.fasterxml.jackson.core.JsonParser;
import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.json.*;

import java.io.InputStream;

public class BegCommand extends Command {

    public BegCommand(Apollo18 bot) {
        super(bot);
        this.name = "beg";
        this.description = "Beg for some bytes!";
        this.category = Category.ECONOMY;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        Database db = bot.getDatabase();
        InputStream is = getClass().getResourceAsStream("/responses/economyResponses.json");

        if (is == null) throw new NullPointerException("Cannot find file");

        JSONTokener tokener = new JSONTokener(is);
        JSONObject responses = new JSONObject(tokener);

        JSONArray successResponses = responses.getJSONArray("begSuccess");
        JSONArray failResponses = responses.getJSONArray("begFail");

        int chance = (int) (Math.random() * 10) + 1;
        if (chance >= 1 && chance <= 3) {
            int randBytes = (int) (Math.random() * (3 - 1) + 1) + 1;
            db.addBytes(event.getUser().getId(), randBytes);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Success");
            embed.addField(successResponses.getString((int) (Math.random() * successResponses.length())), "✅ - You were given <:byte:858172448900644874> " + randBytes + " bytes!", false);
            embed.setFooter("Now go get a job...", event.getUser().getAvatarUrl());
            embed.setColor(EmbedColor.DEFAULT_COLOR);

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        } else {
            EmbedBuilder embed = new EmbedBuilder();

            embed.setTitle("Fail");
            embed.setDescription("❌ - " + failResponses.getString((int) (Math.random() * failResponses.length())));
            embed.setColor(EmbedColor.ERROR_COLOR);

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }

    }
}
