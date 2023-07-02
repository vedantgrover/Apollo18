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
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;

public class TwitterCommand extends Command {
    public TwitterCommand(Apollo18 bot) {
        super(bot);
        this.name = "twitter";
        this.description = "Retrieve Information about a user from Twitter";
        this.category = Category.INFORMATION;

        this.args.add(new OptionData(OptionType.STRING, "username", "The user you want info about", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        String username = event.getOption("username").getAsString();

        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.twitter.com/2/users/by")
                .newBuilder()
                .addPathSegment("username")
                .addPathSegment(username);

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", String.format("Bearer %s", bot.getConfig().get("TWITTER_TOKEN", System.getenv("TWITTER_TOKEN"))))
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            JSONObject data = new JSONObject(responseBody);

            System.out.println(data);

            EmbedBuilder embed = new EmbedBuilder();

            embed.setColor(EmbedColor.DEFAULT_COLOR);
            embed.setTitle(data.getString("name") + " | " + data.getString("username"), data.getString("url"));
            embed.setThumbnail(data.getString("profile_image_url"));
            embed.setDescription(data.getString("description") + "\n\n**__Statistics__**");

            embed.addField("Tweet Count", NumberFormatter.formatLongNumber(data.getJSONObject("public_metrics").getInt("tweet_count")), true);
            embed.addField("Following", NumberFormatter.formatLongNumber(data.getJSONObject("public_metrics").getInt("following_count")), true);
            embed.addField("Followers", NumberFormatter.formatLongNumber(data.getJSONObject("public_metrics").getInt("followers_count")), true);

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        } catch (IOException e) {
            System.err.println(e);
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("We could not find that username.")).setEphemeral(true).queue();
        }
    }
}
