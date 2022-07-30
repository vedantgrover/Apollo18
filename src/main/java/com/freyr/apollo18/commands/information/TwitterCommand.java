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

public class TwitterCommand extends Command {

    public TwitterCommand(Apollo18 bot) {
        super(bot);
        this.name = "twitter";
        this.description = "Retrieve Information about a user from Twitter";
        this.category = Category.INFORMATION;

        this.args.add(new OptionData(OptionType.STRING, "user", "The user you want info about", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        try {
            String user = event.getOption("user").getAsString();
            JSONObject data = getApiData("https://api.twitter.com/2/users/by/username/"+ user +"?user.fields=profile_image_url%2Cpublic_metrics%2Clocation%2Cdescription%2Curl", bot.getConfig().get("TWITTER_TOKEN")).getJSONObject("data");

            EmbedBuilder embed = new EmbedBuilder();

            embed.setColor(EmbedColor.DEFAULT_COLOR);
            embed.setTitle(data.getString("name") + " | " + data.getString("username"), data.getString("url"));
            embed.setThumbnail(data.getString("profile_image_url"));
            embed.setDescription(data.getString("description") + "\n\n**__Statistics__**");

            embed.addField("Tweet Count", NumberFormatter.formatLongNumber(data.getJSONObject("public_metrics").getInt("tweet_count")), true);
            embed.addField("Following", NumberFormatter.formatLongNumber(data.getJSONObject("public_metrics").getInt("following_count")), true);
            embed.addField("Followers", NumberFormatter.formatLongNumber(data.getJSONObject("public_metrics").getInt("followers_count")), true);

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        } catch (Exception e) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("We could not find that user.")).setEphemeral(true).queue();
        }
    }
}
