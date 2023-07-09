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
import org.json.JSONArray;

import java.util.Objects;

public class DictionaryCommand extends Command {
    public DictionaryCommand(Apollo18 bot) {
        super(bot);

        this.name = "define";
        this.description = "Allows you to define any word of your choosing";
        this.category = Category.INFORMATION;
        this.args.add(new OptionData(OptionType.STRING, "word", "The word you want to define", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        String requestedWord = Objects.requireNonNull(event.getOption("word")).getAsString();

        JSONArray data = getApiDataArray("https://www.dictionaryapi.com/api/v3/references/collegiate/json/" + requestedWord + "?key=" + bot.getConfig().get("DICTIONARYAPI", System.getenv("DICTIONARYAPI")));

        if (data.length() <= 0) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("That word doesn't exist")).queue();
            return;
        }

        String word = data.getJSONObject(0).getJSONObject("meta").getString("id");
        String pronunciation = data.getJSONObject(0).getJSONObject("hwi").getString("hw").replace("*", "•");
        String wordType = data.getJSONObject(0).getString("fl");
        JSONArray definition = data.getJSONObject(0).getJSONArray("shortdef");

        EmbedBuilder definitionEmbed = new EmbedBuilder();
        definitionEmbed.setColor(EmbedColor.DEFAULT_COLOR);
        definitionEmbed.setTitle(word);
        definitionEmbed.setDescription(pronunciation + "\n_" + wordType + "_");
        definitionEmbed.addField("Definitions", createDefinitionText(definition), false);

        event.getHook().sendMessageEmbeds(definitionEmbed.build()).queue();

    }

    private String createDefinitionText(JSONArray definitions) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < definitions.toList().size(); i++) {
            result.append("***").append(i + 1).append(".*** ").append(definitions.toList().get(i)).append("\n");
        }

        return result.toString();
    }
}
