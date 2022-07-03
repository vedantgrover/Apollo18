package com.freyr.apollo18.commands.utility;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * This command allows you to send in a suggestion for me to review
 */
public class SuggestCommand extends Command {

    public SuggestCommand(Apollo18 bot) {
        super(bot);
        this.name = "suggest";
        this.description = "Send in a suggestion for the bot or for the server";
        this.category = Category.UTILITY;

        this.args.add(new OptionData(OptionType.STRING, "suggestion", "Your Suggestion", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        String suggestion = event.getOption("suggestion").getAsString();

        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor(event.getUser().getName(), null, event.getUser().getAvatarUrl());
        embed.addField("Suggestion", suggestion, false);
        embed.addField("Server", event.getGuild().getName(), false);
        embed.setColor(EmbedColor.DEFAULT_COLOR);

        event.getGuild().getChannelById(MessageChannel.class, 854150407655784448L).sendMessageEmbeds(embed.build()).queue();
        event.getJDA().getUserById(622506118551437322L).openPrivateChannel().flatMap(channel -> channel.sendMessageEmbeds(embed.build())).queue();
        event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("Suggestion sent to developer")).setEphemeral(true).queue();
    }
}
