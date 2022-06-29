package com.freyr.apollo18.commands.utility;

import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.Instant;

public class ReportBugCommand extends Command {

    public ReportBugCommand() {
        super();
        this.name = "report";
        this.description = "Report a bug!";
        this.category = Category.UTILITY;

        this.args.add(new OptionData(OptionType.STRING, "bug", "Please describe the bug you found", true));
        this.args.add(new OptionData(OptionType.ATTACHMENT, "example", "You can send in an image for us to look at.", false));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        String bug = event.getOption("bug").getAsString();
        OptionMapping example = event.getOption("example");

        EmbedBuilder embed = new EmbedBuilder().setTitle("Bug Report!").setDescription("Bug Report Submitted by: " + event.getUser().getAsMention()).addField("Time", "<t:" + Instant.now().getEpochSecond() + ":F>", false).addField("Description", bug, false).setColor(EmbedColor.DEFAULT_COLOR);
        if (example != null) {
            embed.setImage(example.getAsAttachment().getUrl());
        }

        //noinspection ConstantConditions
        event.getGuild().getChannelById(MessageChannel.class, 988664675480797244L).sendMessageEmbeds(embed.build()).queue();

        MessageEmbed replyEmbed = new EmbedBuilder().setDescription(":white_check_mark: - **Your report has been sent to the bot developer!**").setColor(EmbedColor.DEFAULT_COLOR).build();
        event.getHook().sendMessageEmbeds(replyEmbed).queue();
    }
}
