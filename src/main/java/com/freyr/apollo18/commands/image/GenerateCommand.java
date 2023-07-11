package com.freyr.apollo18.commands.image;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Objects;

public class GenerateCommand extends Command {

    public GenerateCommand(Apollo18 bot) {
        super(bot);

        this.name = "generate";
        this.description = "Uses the DALL-E to generate AI art";
        this.category = Category.IMAGE;
        this.args.add(new OptionData(OptionType.STRING, "prompt", "The description of the image", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        OkHttpClient client = new OkHttpClient();

        // OpenAI API endpoint
        String url = "https://api.openai.com/v1/images/generations";

        // OpenAI API request payload
        String requestBody = "{\"prompt\":\"" + Objects.requireNonNull(event.getOption("prompt")).getAsString() + "\", \"n\": 1, \"size\": \"256x256\"}";

        // Create the request
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer sk-" + bot.getConfig().get("OPENAI_KEY", System.getenv("OPENAI_KEY")))
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .build();

        // Send the request
        try (Response response = client.newCall(request).execute()) {
            // Handle the response
            JSONObject responseBody = new JSONObject(response.body().string());
            System.out.println(responseBody);
            event.getHook().sendMessage(responseBody.getJSONArray("data").getJSONObject(0).getString("url")).queue();
        } catch (SocketTimeoutException e) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("It took too long to generate your image...")).queue();
        } catch (IOException e) {
            System.err.println(e);

            event.getHook().sendMessageEmbeds(EmbedUtils.createError("Something went wrong while generating your image")).queue();
        }
    }
}
