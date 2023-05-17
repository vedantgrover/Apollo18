package com.freyr.apollo18.commands.dev;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class OS extends Command {

    public OS(Apollo18 bot) {
        super(bot);
        this.name = "os";
        this.description = "Shows where the bot is currently running";
        this.devOnly = true;
    }
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        event.getHook().sendMessage(System.getProperty("os.name")).queue();
    }
}
