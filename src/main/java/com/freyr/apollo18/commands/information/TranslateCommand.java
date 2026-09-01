package com.freyr.apollo18.commands.information;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import com.neovisionaries.i18n.LanguageCode;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * This command translates any text to your desired language
 *
 * @author Freyr
 */
public class TranslateCommand extends Command {

    private static final Logger logger = LoggerFactory.getLogger(TranslateCommand.class);

    private final OkHttpClient client = new OkHttpClient();

    public TranslateCommand(Apollo18 bot) {
        super(bot);
        this.name = "translate";
        this.description = "Translate any text to another language!";
        this.category = Category.INFORMATION;

        this.args.add(new OptionData(OptionType.STRING, "text", "The text you want translated", true));
        this.args.add(new OptionData(OptionType.STRING, "language", "The language", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        String userLanguage = event.getOption("language").getAsString();
        String text = event.getOption("text").getAsString();

        LanguageCode[] languageCodes = LanguageCode.values();

        String language = "";
        for (LanguageCode code : languageCodes) {
            if (code.getName().equalsIgnoreCase(userLanguage)) {
                language = code.toString();
            }
        }

        String subscriptionKey = bot.getConfig().get("AZURETRANSLATORKEY", System.getenv("AZURETRANSLATORKEY"));

        // Request URL and body
        String url = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&to=" + language;
        String requestBody = "[{'Text':'" + text + "'}]";

        MediaType mediaType = MediaType.parse("application/json; charset=UTF-8");
        RequestBody body = RequestBody.create(requestBody, mediaType);
        // Create the request
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                .addHeader("Ocp-Apim-Subscription-Region", "westus")
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .post(body)
                .build();

        // Send the request
        try (Response response = client.newCall(request).execute()) {
            // Handle the response
            JSONArray responseBody = new JSONArray(response.body().string());
            logger.debug("Response: {}", responseBody);

            event.getHook().sendMessage(responseBody.getJSONObject(0).getJSONArray("translations").getJSONObject(0).getString("text")).queue();
        } catch (IOException | JSONException e) {
            logger.error("Error translating text to {}", language, e);

            event.getHook().sendMessageEmbeds(EmbedUtils.createError("That language doesn't exist")).queue();
        }

    }
}
