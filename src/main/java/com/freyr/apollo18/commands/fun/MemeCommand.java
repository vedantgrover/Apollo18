package com.freyr.apollo18.commands.fun;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
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

        // Adding different subreddit options
        OptionData optionData = new OptionData(OptionType.STRING, "type", "Specify a type of meme", false);
        optionData.addChoice("dankmeme", "dankmeme");
        optionData.addChoice("surreal", "surreal");
        optionData.addChoice("me_irl", "me_irl");
        optionData.addChoice("wholesome", "wholesome");
        this.args.add(optionData);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue(); // Asking discord to wait longer
        OptionMapping type = event.getOption("type"); // Getting the type (could be null if not given)
        String memeAPI = "https://meme-api.herokuapp.com/gimme"; // Initializing the base string

        // Adding the subreddit if it is not null
        if (type != null) {
            memeAPI += "/" + type.getAsString();
        }

        // Getting the data
        JSONObject data = getApiData(memeAPI);

        // Building the embed
        EmbedBuilder memeEmbed = new EmbedBuilder();
        memeEmbed.setColor(EmbedColor.DEFAULT_COLOR);
        memeEmbed.setTitle(data.getString("title"), data.getString("postLink"));
        memeEmbed.setDescription("Created by: " + data.getString("author"));
        memeEmbed.setImage(data.getString("url"));
        memeEmbed.setFooter("👍 - " + data.getInt("ups"));

        event.getHook().sendMessageEmbeds(memeEmbed.build()).queue();
    }
}
