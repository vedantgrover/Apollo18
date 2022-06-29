package com.freyr.apollo18.commands.utility;

import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class PingCommand extends Command {

    public PingCommand() {
        super();
        this.name = "ping";
        this.description = "Returns the latency of the bot and the Discord API";
        this.category = Category.UTILITY;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        long time = System.currentTimeMillis();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(EmbedColor.DEFAULT_COLOR);
        embed.setDescription(":signal_strength: - Calculating...");
        event.getHook().sendMessageEmbeds(embed.build()).queue(m -> {
            long latency = System.currentTimeMillis() - time;
            EmbedBuilder latencyEmbed = new EmbedBuilder();
            latencyEmbed.setTitle(":ping_pong: Pong!");
            latencyEmbed.setColor(EmbedColor.DEFAULT_COLOR);
            latencyEmbed.addField("Bot Latency", latency + "ms", false);
            latencyEmbed.addField("Discord API", event.getJDA().getGatewayPing() + "ms", false);
            latencyEmbed.setFooter("Requested by " + event.getUser().getName());
            m.editMessageEmbeds(latencyEmbed.build()).queue();
        });
    }
}
