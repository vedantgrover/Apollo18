package com.freyr.apollo18.commands.dev;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CreateDefaultJob extends Command {

    public CreateDefaultJob(Apollo18 bot) {
        super(bot);
        this.name = "create-default-job";
        this.description = "Create a default job";
        this.devOnly = true;

        this.args.add(new OptionData(OptionType.STRING, "business-code", "The code of the business", true));
        this.args.add(new OptionData(OptionType.STRING, "job-name", "Name of job", true));
        this.args.add(new OptionData(OptionType.STRING, "job-description", "Description of job", true));
        this.args.add(new OptionData(OptionType.INTEGER, "salary", "Job salary", true));
        this.args.add(new OptionData(OptionType.INTEGER, "days-till-fire", "Number of days before fire", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        Database db = bot.getDatabase();
        String businessCode = event.getOption("business-code").getAsString().toUpperCase();
        String jobName = event.getOption("job-name").getAsString();
        String jobDescription = event.getOption("job-description").getAsString();
        int salary = event.getOption("salary").getAsInt();
        int daysTillFire = event.getOption("days-till-fire").getAsInt();

        if (jobName.length() > 20) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("Please limit your business name to 15 characters")).queue();
            return;
        }

        if (jobDescription.length() > 50) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("Please limit your description to 50 characters")).queue();
            return;
        }

        if (db.getJob(businessCode, jobName) != null) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("Please create a unique stock code")).queue();
            return;
        }

        try {
            db.createDefaultJob(businessCode, jobName, jobDescription, salary, daysTillFire);
        } catch (Exception e) {
            System.err.println(e);
        }

        event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess(jobName + " has been created for " + businessCode)).queue();
    }
}
