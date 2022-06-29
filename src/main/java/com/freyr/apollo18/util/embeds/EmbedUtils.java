package com.freyr.apollo18.util.embeds;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class EmbedUtils {

    public static MessageEmbed createError(String error) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("❌ - " + error);
        embed.setColor(EmbedColor.ERROR_COLOR);

        return embed.build();
    }

    public static MessageEmbed createSuccess(String succ) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("**:white_check_mark: - " + succ + "**");
        embed.setColor(EmbedColor.DEFAULT_COLOR);

        return embed.build();
    }
}
