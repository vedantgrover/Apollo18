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

public class OhNoCommand extends Command {

    public OhNoCommand(Apollo18 bot) {
        super(bot);

        this.name = "oh-no";
        this.description = "Adds stupid text to a cartoon";
        this.category = Category.IMAGE;
        this.args.add(new OptionData(OptionType.STRING, "text", "The stupid text", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        String text = Objects.requireNonNull(event.getOption("text")).getAsString();
        JSONObject data = postApiData(ImageManipulationAPI.TEXT_IMAGE_API_URL, ImageManipulationAPI.makeRequestBody(null, null, text, "ohno", "text"));

        event.getHook().sendMessage(data.getString("url")).queue();
    }
}
