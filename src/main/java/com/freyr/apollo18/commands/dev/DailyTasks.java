package com.freyr.apollo18.commands.dev;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class DailyTasks extends Command {

    public DailyTasks(Apollo18 bot) {
        super(bot);
        this.name = "daily-tasks";
        this.description = "Does the daily tasks";
        this.devOnly = true;
    }

    @Override

    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        Database db = bot.getDatabase();

        db.updateStocks();
        db.dailyWorkChecks();
        event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("Daily tasks done")).queue();
    }
}
