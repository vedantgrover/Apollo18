package com.freyr.apollo18.commands.image;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.json.JSONArray;
import org.json.JSONObject;

public class CatImageCommand extends Command {

    public CatImageCommand(Apollo18 bot) {
        super(bot);

        this.name = "cat";
        this.description = "Gets a random cat image";
        this.category = Category.IMAGE;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        JSONArray data = getApiDataArray("https://api.thecatapi.com/v1/images/search");

        event.getHook().sendMessage(data.getJSONObject(0).getString("url")).queue();
    }
}
