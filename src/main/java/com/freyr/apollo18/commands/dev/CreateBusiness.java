package com.freyr.apollo18.commands.dev;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CreateBusiness extends Command {

    public CreateBusiness(Apollo18 bot) {
        super(bot);
        this.name = "create-business";
        this.description = "Create a default business";
        this.devOnly = true;

        this.args.add(new OptionData(OptionType.STRING, "business-name", "Name of business", true));
        this.args.add(new OptionData(OptionType.STRING, "business-description", "Description of business", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        Database db = bot.getDatabase();
        String businessName = event.getOption("business-name").getAsString();
        String businessDescription = event.getOption("business-description").getAsString();

        if (businessName.length() > 12) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("Please limit your business name to 12 characters")).queue();
            return;
        }

        if (businessDescription.length() > 50) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("Please limit your description to 50 characters")).queue();
            return;
        }

        db.createBusiness(businessName, event.getUser().getId(),businessDescription );

        event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess(businessName + " has been created")).queue();
    }
}
