package com.freyr.apollo18.commands.economy;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CreateUser extends Command {

    public CreateUser(Apollo18 bot) {
        super(bot);

        this.name = "create-user";
        this.description = "Creates a user within the database";
        this.category = Category.ECONOMY;
        this.devOnly = true;

        this.args.add(new OptionData(OptionType.USER, "user", "User you want to add", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        Database db = bot.getDatabase();

        if (db.createUserData(event.getOption("user").getAsUser())) {
            event.getHook().sendMessage(event.getOption("user").getAsUser().getName() + "'s data has been created.").queue();
        } else {
            event.getHook().sendMessage(event.getOption("user").getAsUser().getName() + "'s data already exists.").queue();
        }
    }
}
