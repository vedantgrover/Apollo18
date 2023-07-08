package com.freyr.apollo18.commands.fun;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This command goes through Reddit and finds a meme to give you
 */
public class MemeCommand extends Command {

    public MemeCommand(Apollo18 bot) {
        super(bot);
        this.name = "meme";
        this.description = "Get a random meme from the internet";
        this.category = Category.FUN;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue(); // Asking discord to wait longer
        String memeAPI = "https://www.reddit.com/r/memes/random/.json"; // Initializing the base string

        // Getting the data
        JSONArray data = getApiDataArray(memeAPI);

        JSONObject memeInfo = data.getJSONObject(0).getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data");

        // Building the embed
        EmbedBuilder memeEmbed = new EmbedBuilder();
        memeEmbed.setColor(EmbedColor.DEFAULT_COLOR);
        memeEmbed.setTitle(memeInfo.getString("title"), memeInfo.getString("url"));
        memeEmbed.setDescription("Created by: " + memeInfo.getString("author"));
        memeEmbed.setImage(memeInfo.getString("url"));
        memeEmbed.setFooter("\uD83D\uDC4D " + memeInfo.getInt("ups") + "\t\uD83D\uDC4E " + memeInfo.getInt("downs"));

        event.getHook().sendMessageEmbeds(memeEmbed.build()).queue();
    }
}
