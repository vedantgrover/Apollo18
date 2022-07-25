package com.freyr.apollo18.commands.leveling;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import com.mongodb.client.FindIterable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bson.Document;

import java.util.*;

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
            embed.addField("\uD83D\uDCC8 Leveling", buildLevelingLeaderboard(event.getGuild(), 5, "leveling", getLevelingLeaderboard(event.getGuild())), false);
            embed.addField("<:byte:858172448900644874> Economy", "Coming soon!", false);

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        } else if (choice.getAsString().equals("leveling")) {
            EmbedBuilder embed = new EmbedBuilder();

            embed.setTitle(event.getGuild().getName() + "'s Leveling Leaderboard");
            embed.setThumbnail(event.getGuild().getIconUrl());
            embed.setColor(EmbedColor.DEFAULT_COLOR);
            embed.setDescription(buildLevelingLeaderboard(event.getGuild(), 10, "leveling", getLevelingLeaderboard(event.getGuild())));

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        } else {
            event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("Coming soon!")).queue();
        }
    }

    private String buildLevelingLeaderboard(Guild guild, int limit, String type, HashMap<String, Integer> data) {
        StringBuilder leaderboard = new StringBuilder();

        int counter = 0;
        for (Map.Entry<String, Integer> mapElement : data.entrySet()) {
            leaderboard.append("`").append(counter + 1).append(")` **").append(guild.getMemberById(mapElement.getKey()).getEffectiveName()).append("** - `").append(mapElement.getValue()).append(" xp`\n");
            counter++;
            if (counter >= limit) {
                leaderboard.append("\uD83C\uDF1F More? `/leaderboard ").append(type.toLowerCase()).append("`");
                break;
            }
        }

        return leaderboard.toString();
    }

    private HashMap<String, Integer> getLevelingLeaderboard(Guild guild) {
        HashMap<String, Integer> userData = new HashMap<>();

        for (Member member : guild.getMembers()) {
            if (bot.getDatabase().getUser(member.getId()) != null && bot.getDatabase().getUserLevelingProfile(member.getId(), guild.getId()) != null) {
                userData.put(member.getId(), bot.getDatabase().getUserLevelingProfile(member.getId(), guild.getId()).getInteger("totalXp"));
            }
        }

        return sortByValue(userData);
    }

    private HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer>> list = new LinkedList<>(hm.entrySet());

        // Sort the list
        list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
}
