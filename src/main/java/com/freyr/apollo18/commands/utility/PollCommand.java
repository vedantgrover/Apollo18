package com.freyr.apollo18.commands.utility;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.List;

public class PollCommand extends Command {

    private static final List<String> NUMBER_EMOJIS = Arrays.asList(
            "\u0031\u20E3",
            "\u0032\u20E3",
            "\u0033\u20E3",
            "\u0034\u20E3",
            "\u0035\u20E3",
            "\u0036\u20E3",
            "\u0037\u20E3",
            "\u0038\u20E3",
            "\u0039\u20E3",
            "\uD83D\uDD1F");

    public PollCommand(Apollo18 bot) {
        super(bot);
        this.name = "poll";
        this.description = "Create a poll!";
        this.category = Category.UTILITY;

        this.args.add(new OptionData(OptionType.STRING, "question", "The question you want to poll", true));
        this.args.add(new OptionData(OptionType.STRING, "choices", "Add your own choices. Separate them by ;"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        String question = event.getOption("question").getAsString();

        OptionMapping choices = event.getOption("choices");
        String[] choicesArray;
        if (choices != null) {
            choicesArray = choices.getAsString().strip().split((choices.getAsString().contains("; ") ? "; " : ";"));
        } else {
            choicesArray = new String[]{"Yes", "No"};
        }

        if (choicesArray.length > 10) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("You are limited to 10 choices only")).queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(question);

        StringBuilder choicesText = new StringBuilder();

        for (int i = 0; i < choicesArray.length; i++) {
            choicesText.append(NUMBER_EMOJIS.get(i)).append(" - ").append(choicesArray[i]).append("\n");
        }

        embed.addField("Choices", choicesText.toString(), false);
        embed.setFooter("Created by " + event.getUser().getName());
        embed.setColor(EmbedColor.DEFAULT_COLOR);

        event.getHook().sendMessageEmbeds(embed.build()).queue(msg -> {
            for (int i = 0; i < choicesArray.length; i++) {
                msg.addReaction(NUMBER_EMOJIS.get(i)).queue();
            }
        });
    }
}
