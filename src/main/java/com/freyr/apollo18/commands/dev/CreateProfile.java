package com.freyr.apollo18.commands.dev;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CreateProfile extends Command {

    public CreateProfile(Apollo18 bot) {
        super(bot);
        this.name = "create-profile";
        this.description = "Creates a guild and server profile";
        this.devOnly = true;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        for (Guild guild : event.getJDA().getGuilds()) {
            for (Member member : guild.getMembers()) {
                bot.getDatabase().createUserData(member.getUser());
            }
            bot.getDatabase().createGuildData(guild);
        }
    }
}
