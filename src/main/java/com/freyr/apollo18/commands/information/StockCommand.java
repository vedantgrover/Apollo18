package com.freyr.apollo18.commands.information;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.handlers.BusinessHandler;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class StockCommand extends Command {

    public StockCommand(Apollo18 bot) {
        super(bot);

        this.name = "stock";
        this.description = "Search information about any stock in the market.";
        this.category = Category.INFORMATION;
        this.args.add(new OptionData(OptionType.STRING, "code", "The stock code of the company", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        String code = Objects.requireNonNull(event.getOption("code")).getAsString().toUpperCase();

        JSONObject companyInfo = getCompanyInformation(code);
        JSONObject priceInfo = getPriceInformation(code);
        String logoInfo = "https://img.logokit.com/ticker/" + code + "?token=pk_fre7166a2245ae887658df";

        assert priceInfo != null;
        if (priceInfo.isEmpty() || companyInfo.isEmpty()) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("Could not find `" + code + "`")).queue();
            return;
        }

//        System.out.println(companyInfo);
//        System.out.println(priceInfo);
//        System.out.println(logoInfo);

        String arrow = getArrow(priceInfo.getDouble("priceChange"));

        EmbedBuilder stockInfoEmbed = new EmbedBuilder();

        stockInfoEmbed.setTitle(companyInfo.getString("Name"));
        stockInfoEmbed.setUrl(companyInfo.getString("OfficialSite"));
        stockInfoEmbed.setThumbnail(logoInfo);
        stockInfoEmbed.setDescription(companyInfo.getString("Description"));
        stockInfoEmbed.setColor(EmbedColor.DEFAULT_COLOR);
        stockInfoEmbed.addField("Ticker", "`" + companyInfo.getString("Symbol") + "`", true);
        stockInfoEmbed.addField("Price", "$" + priceInfo.getDouble("lastPrice"), true);
        stockInfoEmbed.addField("Change", "$" + priceInfo.getDouble("priceChange") + " " + arrow, true);

        event.getHook().sendMessageEmbeds(stockInfoEmbed.build()).queue();
    }

    private JSONObject getPriceInformation(String code) {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://realstonks.p.rapidapi.com/stocks/" + code)).header("x-rapidapi-key", bot.getConfig().get("RAPIDAPI_KEY", System.getenv("RAPIDAPI_KEY"))).header("x-rapidapi-host", "realstonks.p.rapidapi.com").method("GET", HttpRequest.BodyPublishers.noBody()).build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            return new JSONObject(response.body());
        } catch (JSONException e) {
            return new JSONObject("{}");
        } catch (IOException | InterruptedException e) {
            return null;
        }
    }

    private JSONObject getCompanyInformation(String code) {
        return getApiData("https://www.alphavantage.co/query?function=OVERVIEW&symbol=" + code + "&apikey=" + bot.getConfig().get("ALPHAVANTAGE", System.getenv("ALPHAVANTAGE")));
    }

    private String getArrow(double change) {
        if (change > 0) {
            return BusinessHandler.upArrow;
        } else if (change < 0) {
            return BusinessHandler.downArrow;
        } else {
            return BusinessHandler.neutral;
        }
    }

}
