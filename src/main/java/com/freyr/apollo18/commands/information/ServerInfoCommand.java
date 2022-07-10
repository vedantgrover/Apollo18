package com.freyr.apollo18.commands.information;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.TimeFormat;

public class ServerInfoCommand extends Command {

    public ServerInfoCommand(Apollo18 bot) {
        super(bot);
        this.name = "server";
        this.description = "Returns the server information";
        this.category = Category.INFORMATION;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        Guild guild = event.getGuild();

        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle(guild.getName());
        embed.setColor(EmbedColor.DEFAULT_COLOR);
        embed.setDescription("Created " + TimeFormat.RELATIVE.format(guild.getTimeCreated().toInstant().toEpochMilli()) + "\n\n**__Statistics__**");
        embed.setThumbnail(guild.getIconUrl());

        embed.addField("Server ID", guild.getId(), true);
        embed.addField("Members (" + guild.getMemberCount() + ")", guild.getBoostCount() + " boosts", true);
        embed.addField("Channels", "**" + guild.getTextChannels().size() + "** Text\n**" + guild.getVoiceChannels().size() + "** Voice", true);

        embed.setFooter("Owned by " + guild.getOwner().getEffectiveName(), guild.getOwner().getAvatarUrl());

        event.getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
