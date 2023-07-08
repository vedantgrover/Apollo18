package com.freyr.apollo18.commands.image.text;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.api.ImageManipulationAPI;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.json.JSONObject;

import java.util.Objects;

public class ClydeCommand extends Command {

    public ClydeCommand(Apollo18 bot) {
        super(bot);

        this.name = "clyde";
        this.description = "Creates a message as Clyde";
        this.category = Category.IMAGE;
        this.args.add(new OptionData(OptionType.STRING, "text", "The text you want clyde to read", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        String text = Objects.requireNonNull(event.getOption("text")).getAsString();
        JSONObject data = postApiData(ImageManipulationAPI.TEXT_IMAGE_API_URL, ImageManipulationAPI.makeRequestBody(null, null, text, "clyde", "text"));

        event.getHook().sendMessage(data.getString("url")).queue();
    }
}
