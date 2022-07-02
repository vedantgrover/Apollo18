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
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class WeatherCommand extends Command {

    public WeatherCommand(Apollo18 bot) {
        super(bot);
        this.name = "weather";
        this.description = "Returns all the weather data for that location";
        this.category = Category.INFORMATION;

        this.args.add(new OptionData(OptionType.STRING, "location", "The location you want the weather for.", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        String location = event.getOption("location").getAsString();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.weatherapi.com/v1/current.json?key=" + bot.getConfig().get("WEATHER_TOKEN", System.getenv("WEATHER_TOKEN")) + "&q=" + URLEncoder.encode(location, StandardCharsets.UTF_8) + "&aqi=yes")).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject data = new JSONObject(response.body());

            EmbedBuilder weatherEmbed = new EmbedBuilder();
            weatherEmbed.setColor(EmbedColor.DEFAULT_COLOR);
            weatherEmbed.setThumbnail("https:" + data.getJSONObject("current").getJSONObject("condition").getString("icon"));
            weatherEmbed.setTitle(data.getJSONObject("location").getString("name") + ", " + data.getJSONObject("location").getString("region"));
            weatherEmbed.setDescription("**" + data.getJSONObject("current").getJSONObject("condition").getString("text") + "**");

            weatherEmbed.addField("Time", "`" + data.getJSONObject("location").get("localtime") + "`", true);
            weatherEmbed.addField("Temperature", data.getJSONObject("current").getDouble("temp_f") + " °F", true);
            String wind = "Wind Speed: " + data.getJSONObject("current").getDouble("wind_mph") + " mph\nDirection: " + data.getJSONObject("current").getString("wind_dir");
            weatherEmbed.addField("Wind", wind, true);
            weatherEmbed.addField("Humidity", data.getJSONObject("current").get("humidity") + "%", true);
            weatherEmbed.addField("UV", String.valueOf(data.getJSONObject("current").getDouble("uv")), true);
            weatherEmbed.addField("Precipitation", data.getJSONObject("current").getDouble("precip_in") + " in", true);

            weatherEmbed.setFooter("Feels like: " + data.getJSONObject("current").getDouble("feelslike_f") + " °F");


            event.getHook().sendMessageEmbeds(weatherEmbed.build()).queue();
        } catch (IOException | InterruptedException e) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("I was unable to get the weather data.")).queue();
            e.printStackTrace();
        }
    }
}
