package com.freyr.apollo18.commands.information;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import com.freyr.apollo18.util.textFormatters.NumberFormatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.json.JSONObject;

public class YouTubeCommand extends Command {

    public YouTubeCommand(Apollo18 bot) {
        super(bot);
        this.name = "youtube";
        this.description = "Search up channel information from YouTube";
        this.category = Category.INFORMATION;

        this.args.add(new OptionData(OptionType.STRING, "channel", "The YouTube Channel you want information about.", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        try {
            String channel = event.getOption("channel").getAsString().replace(" ", "+");

            JSONObject youtubeData = getApiData("https://www.googleapis.com/youtube/v3/search?part=snippet&type=channel&maxResults=1&q=" + channel + "&key=" + bot.getConfig().get("YOUTUBE_TOKEN", System.getenv("YOUTUBE_TOKEN"))).getJSONArray("items").getJSONObject(0).getJSONObject("snippet");
            JSONObject youtubeStats = getApiData("https://www.googleapis.com/youtube/v3/channels?part=statistics&id=" + youtubeData.getString("channelId") + "&key=" + bot.getConfig().get("YOUTUBE_TOKEN", System.getenv("YOUTUBE_TOKEN"))).getJSONArray("items").getJSONObject(0).getJSONObject("statistics");

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(EmbedColor.DEFAULT_COLOR);
            embed.setThumbnail(youtubeData.getJSONObject("thumbnails").getJSONObject("default").getString("url"));
            embed.setTitle(youtubeData.getString("channelTitle"));
            embed.setDescription(youtubeData.getString("description") + "\n\n**Published on: **`" + youtubeData.getString("publishTime") + "`\n\n**__Statistics__**");

            embed.addField("Video Count", NumberFormatter.formatLongNumber(Double.parseDouble(youtubeStats.getString("videoCount"))), true);
            embed.addField("Subscribers", NumberFormatter.formatLongNumber(Double.parseDouble(youtubeStats.getString("subscriberCount"))), true);

            embed.setFooter("View Count: " + NumberFormatter.formatLongNumber(Double.parseDouble(youtubeStats.getString("viewCount"))) + " views", youtubeData.getJSONObject("thumbnails").getJSONObject("default").getString("url"));

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        } catch (Exception e) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("We were unable to find the YouTube Channel")).setEphemeral(true).queue();
        }
    }
}
