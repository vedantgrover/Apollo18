package com.freyr.apollo18.util.embeds;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * This class handles all the repetitive embed creations
 */
public class EmbedUtils {

    /**
     * This method creates a simple error message for the bot to send
     *
     * @param error The text the user wants within the embed
     * @return The fully built error embed.
     */
    public static MessageEmbed createError(String error) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("❌ - " + error);
        embed.setColor(EmbedColor.ERROR_COLOR);

        return embed.build();
    }

    /**
     * This method creates a simple success message for the bot to send
     *
     * @param succ The text the user wants within the embed
     * @return The fully built success embed.
     */
    public static MessageEmbed createSuccess(String succ) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("**:white_check_mark: - " + succ + "**");
        embed.setColor(EmbedColor.DEFAULT_COLOR);

        return embed.build();
    }

    /**
     * This method creates a simple notification message for the bot to send
     *
     * @param notif The text for the notification
     * @return The fully built notification embed
     */
    public static MessageEmbed createNotification(String notif) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Notification");
        embed.setDescription(notif);
        embed.setColor(EmbedColor.DEFAULT_COLOR);

        return embed.build();
    }
}
