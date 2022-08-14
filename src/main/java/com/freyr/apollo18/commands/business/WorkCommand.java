package com.freyr.apollo18.commands.business;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;

public class WorkCommand extends Command {

    public WorkCommand(Apollo18 bot) {
        super(bot);
        this.name = "work";
        this.description = "Work for some bytes!";
        this.category = Category.ECONOMY;
        this.cooldown = 21600;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        Database db = bot.getDatabase();

        InputStream is = getClass().getResourceAsStream("/responses/economyResponses.json");

        if (is == null) throw new NullPointerException("Cannot find file");

        JSONTokener tokener = new JSONTokener(is);
        JSONObject responses = new JSONObject(tokener);

        JSONArray workResponses = responses.getJSONArray("work");

        if (db.work(event.getUser().getId())) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Success");
            embed.addField(workResponses.getString((int) (Math.random() * workResponses.length())), "You were payed <:byte:858172448900644874> `" + db.getJob(db.getUserJob(event.getUser().getId()).getString("business"), db.getUserJob(event.getUser().getId()).getString("job")).getInteger("salary") + " bytes`", false);
            embed.setColor(EmbedColor.DEFAULT_COLOR);
            embed.setFooter("You have worked for " + db.getUser(event.getUser().getId()).get("economy", Document.class).get("job", Document.class).getInteger("daysWorked") + " day(s)");

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        } else {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("You do not have a job! Please set a job first")).queue();
        }
    }
}
