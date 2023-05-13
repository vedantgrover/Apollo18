package com.freyr.apollo18.commands.leveling;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.handlers.BusinessHandler;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.mongodb.client.FindIterable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bson.Document;

public class LeaderboardCommand extends Command {
    public LeaderboardCommand(Apollo18 bot) {
        super(bot);
        this.name = "leaderboard";
        this.description = "Outputs a leaderboard for the server.";
        this.category = Category.LEVELING;

        this.args.add(new OptionData(OptionType.STRING, "type", "Specify if you want leaderboard or economy!").addChoice("leveling", "leveling").addChoice("economy", "economy"));

    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        OptionMapping choice = event.getOption("type");

        if (choice == null) {
            EmbedBuilder embed = new EmbedBuilder();

            embed.setTitle(event.getGuild().getName() + "'s Leaderboards");
            embed.setThumbnail(event.getGuild().getIconUrl());
            embed.setColor(EmbedColor.DEFAULT_COLOR);
            embed.addField("\uD83D\uDCC8 Leveling", buildLeaderboard(event.getGuild(), 5, false), true);
            embed.addField(BusinessHandler.byteEmoji + " Economy", buildLeaderboard(event.getGuild(), 5, true), true);

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        } else if (choice.getAsString().equals("leveling")) {
            EmbedBuilder embed = new EmbedBuilder();

            embed.setTitle(event.getGuild().getName() + "'s Leveling Leaderboard");
            embed.setThumbnail(event.getGuild().getIconUrl());
            embed.setColor(EmbedColor.DEFAULT_COLOR);
            embed.setDescription(buildLeaderboard(event.getGuild(), 10, false));

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        } else {
            EmbedBuilder embed = new EmbedBuilder();

            embed.setTitle(event.getGuild().getName() + "'s Economy Leaderboard");
            embed.setThumbnail(event.getGuild().getIconUrl());
            embed.setColor(EmbedColor.DEFAULT_COLOR);
            embed.setDescription(buildLeaderboard(event.getGuild(), 10, true));

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }
    }

    private String buildLeaderboard(Guild guild, int limit, boolean isEconomy) {
        StringBuilder result = new StringBuilder();
        FindIterable<Document> data = (isEconomy) ? null : bot.getDatabase().getLevelingLeaderboard(guild.getId(), limit);

        int num = 0;
        if (data == null) {
            for (Document doc : bot.getDatabase().getEconomyLeaderboard(guild.getId(), limit)) {
                result.append("`").append(num + 1).append(")` **__").append(guild.getMemberById(doc.getString("userID")).getEffectiveName()).append("__** - **Net Worth:** " + BusinessHandler.byteEmoji + " `").append(bot.getDatabase().getNetWorth(doc.getString("userID"))).append(" bytes`\n");
                num++;
            }
        } else {
            for (Document doc : data) {
                result.append("`").append(num + 1).append(")` **__").append(guild.getMemberById(doc.getString("userID")).getEffectiveName()).append("__** - **XP:** `").append(bot.getDatabase().getUserLevelingProfile(doc.getString("userID"), guild.getId()).getInteger("totalXp")).append("`\n");
                num++;
            }
        }

        return result.toString();
    }
}
