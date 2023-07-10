package com.freyr.apollo18.commands.information;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.listeners.ButtonListener;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
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
        String requestedWord = Objects.requireNonNull(event.getOption("word")).getAsString();

        JSONArray data = getApiDataArray("https://www.dictionaryapi.com/api/v3/references/collegiate/json/" + requestedWord + "?key=" + bot.getConfig().get("DICTIONARYAPI", System.getenv("DICTIONARYAPI")));

        if (data.length() <= 0) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("That word doesn't exist")).queue();
            return;
        }

        List<MessageEmbed> definitionEmbeds = new ArrayList<>();

        for (int i = 0; i < data.length(); i++) {
            String word = data.getJSONObject(i).getJSONObject("meta").getString("id");
            String pronunciation = data.getJSONObject(i).getJSONObject("hwi").getString("hw").replace("*", "•");
            String wordType = data.getJSONObject(i).getString("fl");
            JSONArray definition = data.getJSONObject(i).getJSONArray("shortdef");

            EmbedBuilder definitionEmbed = new EmbedBuilder();
            definitionEmbed.setColor(EmbedColor.DEFAULT_COLOR);
            definitionEmbed.setTitle(word);
            definitionEmbed.setDescription(pronunciation + "\n_" + wordType + "_");
            definitionEmbed.addField("Definitions", createDefinitionText(definition), false);

            definitionEmbeds.add(definitionEmbed.build());
        }

        // Send paginated help menu
        ReplyCallbackAction action = event.replyEmbeds(definitionEmbeds.get(0));
        if (definitionEmbeds.size() > 1) {
            ButtonListener.sendPaginatedMenu(event.getUser().getId(), action, definitionEmbeds);
            return;
        }
        action.queue();
    }

    private String createDefinitionText(JSONArray definitions) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < definitions.toList().size(); i++) {
            result.append("***").append(i + 1).append(".*** ").append(definitions.toList().get(i)).append("\n");
        }

        return result.toString();
    }
}
