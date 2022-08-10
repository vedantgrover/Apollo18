package com.freyr.apollo18.commands.business;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.util.embeds.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.bson.Document;

import java.util.Map;

public class BusinessCommand extends Command {

    public BusinessCommand(Apollo18 bot) {
        super(bot);
        this.name = "business";
        this.description = "Here you can find all of the business commands!";
        this.category = Category.BUSINESS;

        this.subCommands.add(new SubcommandData("show", "Shows all of the businesses"));
        this.subCommands.add(new SubcommandData("info", "Shows the info about a business").addOption(OptionType.STRING, "code", "The stock code", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        Database db = bot.getDatabase();

        switch (event.getSubcommandName()) {
            case "show" -> {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Businesses");
                embed.setDescription("Here are all of the businesses that you can invest in!");

                for (Map.Entry<String, String> business : db.getBusinesses().entrySet()) {
                    embed.addField(business.getKey(), business.getValue(), true);
                }

                embed.setColor(EmbedColor.DEFAULT_COLOR);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
            }

            case "info" -> {
                String code = event.getOption("code").getAsString().toUpperCase();
                Document business = db.getBusiness(code);

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(EmbedColor.DEFAULT_COLOR);
                embed.setThumbnail(business.getString("logo"));
                embed.setTitle(business.getString("name"));
                embed.setDescription(business.getString("description"));
                embed.addField("Stock Info", "Price: <:byte:858172448900644874> `" + business.get("stock", Document.class).getInteger("currentPrice") + " bytes` " + business.get("stock", Document.class).getString("arrowEmoji") + " `(" + business.get("stock", Document.class).getInteger("change") + ")`\nCode: `" + business.getString("stockCode") + "`", false);

                event.getHook().sendMessageEmbeds(embed.build()).queue();
            }
        }
    }
}
