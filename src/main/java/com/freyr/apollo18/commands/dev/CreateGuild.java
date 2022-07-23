package com.freyr.apollo18.commands.dev;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CreateGuild extends Command {

    public CreateGuild(Apollo18 bot) {
        super(bot);

        this.name = "create-guild";
        this.description = "Creates a user within the database";
        this.devOnly = true;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        Database db = bot.getDatabase();

        db.createGuildData(event.getGuild());

        for (Member member : event.getGuild().getMembers()) {
            db.createUserData(member.getUser());
        }

        event.getHook().sendMessage(event.getGuild().getName() + "'s data has been created.").queue();
    }
}
