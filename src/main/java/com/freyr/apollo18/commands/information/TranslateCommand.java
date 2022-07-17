package com.freyr.apollo18.commands.information;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

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

    private static final Map<String, String> languages = new HashMap<>(); // Holds all the language names with its corresponding ISO language code.

    public TranslateCommand(Apollo18 bot) {
        super(bot);
        this.name = "translate";
        this.description = "Translate any text to another language!";
        this.category = Category.INFORMATION;

        this.args.add(new OptionData(OptionType.STRING, "language", "ISO Language Code", true));
        this.args.add(new OptionData(OptionType.STRING, "text", "The text you want translated", true));
        this.args.add(new OptionData(OptionType.STRING, "from", "The language you are translating from", false));

        // Adding all the languages and their ISO language codes into the map.
        for (String lang : Locale.getISOLanguages()) {
            Locale l = new Locale(lang);
            languages.put(l.getDisplayLanguage().toLowerCase(), lang);
        }
    }

    /**
     * Using an API that I made, the bot translates the text into a language and returns the string (the translated text).
     *
     * @param langTo The ISO Language code. This tells the API what language to translate to.
     * @param text   The text you want to translate
     * @return The translated text
     * @throws IOException when the text cannot be encoded due to an invalid encoder
     */
    private static String translate(String langTo, String langFrom, String text) throws Exception {

        if ((!languages.containsValue(langTo) && !languages.containsKey(langTo)) || (!languages.containsValue(langFrom) && !languages.containsKey(langFrom))) throw new Exception("Incorrect Language");

        String urlStr = "https://script.google.com/macros/s/AKfycbwGTyjBTwcPLAdt2EErzRdVA9CN-cngEglSEg0XcpzS6YZopWfuq-RcYl_fCe8kXVnk/exec?q=" + URLEncoder.encode(text, "UTF-8") + "&target=" + langTo + "&source=" + langFrom;
        URL url = new URL(urlStr);
        StringBuilder response = new StringBuilder();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        String lang = event.getOption("language").getAsString();
        String text = event.getOption("text").getAsString();

        try {
            EmbedBuilder embed = new EmbedBuilder();

            String language = (event.getOption("from") != null) ? event.getOption("from").getAsString().toUpperCase() : "";

            embed.setAuthor(event.getUser().getName(), null, event.getUser().getAvatarUrl());
            embed.addField("Original `(" + language + ")`", text, false);
            embed.addField("Translated `(" + lang.toUpperCase() + ")`", translate((languages.get(lang.toLowerCase()) != null) ? languages.get(lang.toLowerCase()):lang.toLowerCase(), (event.getOption("from") == null) ? "" : languages.get(event.getOption("from").getAsString()), text).replace("&#39;", "'"), false);
            embed.setColor(EmbedColor.DEFAULT_COLOR);

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        } catch (Exception e) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("`" + lang + "` is not a language")).queue();
        }

    }
}
