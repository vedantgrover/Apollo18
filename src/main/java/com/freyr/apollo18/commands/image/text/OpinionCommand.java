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

public class OpinionCommand extends Command {

    public OpinionCommand(Apollo18 bot) {
        super(bot);

        this.name = "opinion";
        this.description = "Creates a message as Clyde";
        this.category = Category.IMAGE;
        this.args.add(new OptionData(OptionType.STRING, "text", "The text you want clyde to read", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        String text = Objects.requireNonNull(event.getOption("text")).getAsString();
        JSONObject data = postApiData(ImageManipulationAPI.TEXT_IMAGE_API_URL, ImageManipulationAPI.makeRequestBody(event.getUser().getAvatarUrl(), null, text, "opinion", "text"));

        event.getHook().sendMessage(data.getString("url")).queue();
    }
}
