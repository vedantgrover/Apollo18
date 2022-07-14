package com.freyr.apollo18.commands.utility;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * A simple math command using the Javaluator libraries
 */
public class MathCommand extends Command {

    public MathCommand(Apollo18 bot) {
        super(bot);
        this.name = "math";
        this.description = "Evaluate an expression through the bot!";
        this.category = Category.UTILITY;

        this.args.add(new OptionData(OptionType.STRING, "expression", "Math Expression", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        String expression = event.getOption("expression").getAsString(); // Getting the expression

        try {
            DoubleEvaluator evaluator = new DoubleEvaluator(); // Getting an instance of the evaluator

            // Creating the embed
            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(event.getUser().getName(), null, event.getUser().getAvatarUrl());
            embed.addField("Expression", "`" + expression + "`", false);
            embed.addField("Solution", String.valueOf(evaluator.evaluate(expression)), false); // Evaluating the expression
            embed.setColor(EmbedColor.DEFAULT_COLOR);

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        } catch (Exception e) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("`" + expression + "` is not a valid expression")).queue(); // If the expression is inaccurate or something goes wrong, then it will send an error message in discord.
        }
    }
}
