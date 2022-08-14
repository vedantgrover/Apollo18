package com.freyr.apollo18.commands.dev;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class CreateDefaultBusiness extends Command {

    public CreateDefaultBusiness(Apollo18 bot) {
        super(bot);
        this.name = "create-default-business";
        this.description = "Create a default business";
        this.devOnly = true;

        this.args.add(new OptionData(OptionType.STRING, "business-name", "Name of business", true));
        this.args.add(new OptionData(OptionType.STRING, "business-description", "Description of business", true));
        this.args.add(new OptionData(OptionType.STRING, "stock-ticker", "Link business to stock", true));
        this.args.add(new OptionData(OptionType.STRING, "stock-code", "The code for your stock", true));
        this.args.add(new OptionData(OptionType.STRING, "logo", "The logo"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        Database db = bot.getDatabase();
        String businessName = event.getOption("business-name").getAsString();
        String businessDescription = event.getOption("business-description").getAsString();
        String ticker = event.getOption("stock-ticker").getAsString().toUpperCase();
        String stockCode = event.getOption("stock-code").getAsString().toUpperCase();

        if (businessName.length() > 12) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("Please limit your business name to 12 characters")).queue();
            return;
        }

        if (businessDescription.length() > 50) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("Please limit your description to 50 characters")).queue();
            return;
        }

        if (ticker.length() > 4 || stockCode.length() > 4) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("Please limit the ticker/stock code to 4 characters")).queue();
            return;
        }

        if (db.getBusiness(stockCode) != null) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("Please create a unique stock code")).queue();
            return;
        }

        try {
            db.createDefaultBusiness(businessName, businessDescription, ticker, stockCode, (event.getOption("logo") == null) ? null : event.getOption("logo").getAsString());
        } catch (Exception e) {
            System.err.println(e);
        }

        event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess(businessName + " has been created")).queue();
    }
}
