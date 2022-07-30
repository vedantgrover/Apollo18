package com.freyr.apollo18.commands.fun;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class UrbanDictionaryCommand extends Command {

    public UrbanDictionaryCommand(Apollo18 bot) {
        super(bot);
        this.name = "urban";
        this.description = "Search up a term on the urban dictionary";
        this.category = Category.FUN;

        this.args.add(new OptionData(OptionType.STRING, "search", "Your search query", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        try {
            JSONObject data = getApiData("https://api.urbandictionary.com/v0/define?term=" + URLEncoder.encode(event.getOption("search").getAsString(), StandardCharsets.UTF_8)).getJSONArray("list").getJSONObject(0);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(data.getString("word"), data.getString("permalink"));
            embed.setDescription(data.getString("definition"));
            embed.addField("Example", data.getString("example"), false);
            embed.setFooter("👍 " + data.getInt("thumbs_up") + " || " + data.getString("author") + " || 👎 " + data.getInt("thumbs_down"));

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        } catch (Exception e) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("Unable to find definition of **" + event.getOption("search").getAsString() + "**")).queue();
        }
    }
}
