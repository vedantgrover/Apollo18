package com.freyr.apollo18.commands.image;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class ImageGenerationCommand extends Command {

    public ImageGenerationCommand(Apollo18 bot) {
        super(bot);

        this.name = "generate";
        this.description = "Generate an image through text";
        this.category = Category.IMAGE;
        this.args.add(new OptionData(OptionType.STRING, "description", "Image description", true));
        this.args.add(new OptionData(OptionType.INTEGER, "number", "Number of Images you want").setMinValue(1).setMaxValue(5));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        int numberOfImages = (event.getOption("number") == null) ? 1: Objects.requireNonNull(event.getOption("number")).getAsInt();

        JSONObject requestBodyJSON = new JSONObject();
        requestBodyJSON.put("prompt", Objects.requireNonNull(event.getOption("description")).getAsString());
        requestBodyJSON.put("n", numberOfImages);
        requestBodyJSON.put("size", "256x256");

        String requestBody = requestBodyJSON.toString();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/images/generations"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + bot.getConfig().get("OPENAI_KEY", System.getenv("OPENAI_KEY")))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject data = new JSONObject(response.body());

            JSONArray image = data.getJSONArray("data");

            for (int i = 0; i < image.length(); i++) {
                JSONObject urlHolder = image.getJSONObject(i);

                String url = urlHolder.getString("url");
                event.getHook().sendMessage(url).queue();
            }
        } catch (Exception e) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("Something went wrong while generating your image.")).queue();
            System.err.println(e);
        }
    }
}
