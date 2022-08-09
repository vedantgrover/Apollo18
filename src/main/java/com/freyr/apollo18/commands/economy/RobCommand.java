package com.freyr.apollo18.commands.economy;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;

public class RobCommand extends Command {

    public RobCommand(Apollo18 bot) {
        super(bot);
        this.name = "rob";
        this.description = "Rob another user of their bytes";
        this.category = Category.ECONOMY;
        this.cooldown = 0;

        this.args.add(new OptionData(OptionType.USER, "victim", "Who are you robbing?", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        Database db = bot.getDatabase();
        User victim = event.getOption("victim").getAsUser();

        InputStream is = getClass().getResourceAsStream("/responses/economyResponses.json");

        if (is == null) throw new NullPointerException("Cannot find file");

        JSONTokener tokener = new JSONTokener(is);
        JSONObject responses = new JSONObject(tokener);

        JSONArray successResponses = responses.getJSONArray("robSuccess");
        JSONArray failResponses = responses.getJSONArray("robFail");

        if (victim.isBot()) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("You are robbing a bot. They have no money and no life.")).queue();
            return;
        }

        if (db.getBalance(victim.getId()) < 12) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError(victim.getName() + " doesn't have enough money!")).queue();
            return;
        }

        int chance = (int) (Math.random() * 10) + 1;
        if (chance >= 1 && chance <= 3) {
            String randomResponse = successResponses.getString((int) (Math.random() * successResponses.length())).replace("[member]", event.getUser().getName());
            int bytesStolen = (int) (Math.random() * (12 - 5) + 1) + 5;

            int robberOldBytes = db.getBalance(event.getUser().getId());
            int victimOldBytes = db.getBalance(victim.getId());

            db.addBytes(event.getUser().getId(), bytesStolen);
            db.removeBytes(victim.getId(), bytesStolen);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Success");
            embed.addField(randomResponse, ":white_check_mark: - You robbed " + bytesStolen + " bytes from " + victim.getName(), false);
            embed.setColor(EmbedColor.DEFAULT_COLOR);

            event.getHook().sendMessageEmbeds(embed.build()).queue();

            if (db.getNotificationToggle(victim.getId())) {
                event.getJDA().getUserById(victim.getId()).openPrivateChannel().flatMap(channel -> channel.sendMessageEmbeds(EmbedUtils.createNotification(event.getUser().getName() + " has just robbed <:byte:858172448900644874> " + bytesStolen + " bytes from you!"))).queue();
            }

            db.createTransaction(event.getUser().getId(), "Robbery / Robber", robberOldBytes, db.getBalance(event.getUser().getId()));
            db.createTransaction(victim.getId(), "Robbery / Victim", victimOldBytes, db.getBalance(victim.getId()));
        } else {
            String randomResponse = failResponses.getString((int) (Math.random() * failResponses.length())).replace("[member]", event.getUser().getName());

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Failure");
            embed.setDescription(":x: - " + randomResponse);
            embed.setColor(EmbedColor.DEFAULT_COLOR);

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }
    }
}
