package com.freyr.apollo18.commands.image;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
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
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        JSONObject requestBodyJSON = new JSONObject();
        requestBodyJSON.put("prompt", Objects.requireNonNull(event.getOption("description")).getAsString());
        requestBodyJSON.put("size", "512x512");

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

            String image = data.getJSONArray("data").getJSONObject(0).getString("url");

            event.getHook().sendMessage(image).queue();
        } catch (InterruptedException | IOException e) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("Something went wrong while generating your image.")).queue();
            System.err.println(e);
        }
    }
}
