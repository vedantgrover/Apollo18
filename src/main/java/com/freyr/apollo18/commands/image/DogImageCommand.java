package com.freyr.apollo18.commands.image;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.json.JSONObject;

public class DogImageCommand extends Command {

    public DogImageCommand(Apollo18 bot) {
        super(bot);

        this.name = "dog";
        this.description = "Gets a random dog image";
        this.category = Category.IMAGE;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        JSONObject data = getApiData("https://dog.ceo/api/breeds/image/random");

        event.getHook().sendMessage(data.getString("message")).queue();
    }
}
