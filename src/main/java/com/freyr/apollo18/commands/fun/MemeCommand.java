package com.freyr.apollo18.commands.fun;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MemeCommand extends Command {

    public MemeCommand(Apollo18 bot) {
        super(bot);
        this.name = "meme";
        this.description = "Get a random meme from the internet";
        this.category = Category.FUN;

        OptionData optionData = new OptionData(OptionType.STRING, "type", "Specify a type of meme", false);
        optionData.addChoice("dankmeme", "dankmeme");
        optionData.addChoice("surreal", "surreal");
        optionData.addChoice("me_irl", "me_irl");
        optionData.addChoice("wholesome", "wholesome");
        this.args.add(optionData);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        OptionMapping type = event.getOption("type");
        String memeAPI = "https://meme-api.herokuapp.com/gimme";

        if (type != null) {
            memeAPI += "/" + type.getAsString();
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(memeAPI)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject data = new JSONObject(response.body());

            System.out.println(data);

            EmbedBuilder memeEmbed = new EmbedBuilder();
            memeEmbed.setColor(EmbedColor.DEFAULT_COLOR);
            memeEmbed.setTitle(data.getString("title"), data.getString("postLink"));
            memeEmbed.setDescription("Created by: " + data.getString("author"));
            memeEmbed.setImage(data.getString("url"));
            memeEmbed.setFooter("👍 - " + data.getInt("ups"));

            event.getHook().sendMessageEmbeds(memeEmbed.build()).queue();
        } catch (IOException | InterruptedException e) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("There was an error while getting your meme.")).queue();
            e.printStackTrace();
        }
    }
}
