package com.freyr.apollo18.commands.information;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.TimeFormat;

public class UserInfoCommand extends Command {

    public UserInfoCommand(Apollo18 bot) {
        super(bot);
        this.name = "user";
        this.description = "Returns information about a user";
        this.category = Category.INFORMATION;

        this.args.add(new OptionData(OptionType.USER, "user", "User you want info about"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        User user = (event.getOption("user") == null) ? event.getUser():event.getOption("user").getAsUser();

        event.getGuild().retrieveMember(user).queue(member -> {
            EmbedBuilder embed = new EmbedBuilder();

            embed.setColor(EmbedColor.DEFAULT_COLOR);
            embed.setAuthor(user.getName(), null, user.getAvatarUrl());
            embed.setDescription("**ID:** " + user.getId());

            embed.addField("Joined Discord", TimeFormat.RELATIVE.format(member.getTimeCreated().toInstant().toEpochMilli()), true);
            embed.addField("Joined Server", TimeFormat.RELATIVE.format(member.getTimeJoined().toInstant().toEpochMilli()), true);

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        });
    }
}
