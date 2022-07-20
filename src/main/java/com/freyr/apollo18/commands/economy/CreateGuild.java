package com.freyr.apollo18.commands.economy;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CreateGuild extends Command {

    public CreateGuild(Apollo18 bot) {
        super(bot);

        this.name = "create-guild";
        this.description = "Creates a user within the database";
        this.category = Category.ECONOMY;
        this.devOnly = true;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        Database db = bot.getDatabase();

        db.createGuildData(event.getGuild());

        event.getHook().sendMessage(event.getGuild().getName() + "'s data has been created.").queue();
    }
}
