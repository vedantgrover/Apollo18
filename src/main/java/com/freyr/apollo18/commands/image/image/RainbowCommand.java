package com.freyr.apollo18.commands.image.image;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.api.ImageManipulationAPI;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.json.JSONObject;

import java.util.Objects;

public class RainbowCommand extends Command {

    public RainbowCommand(Apollo18 bot) {
        super(bot);

        this.name = "rainbow";
        this.description = "Applies a rainbow over a pfp";
        this.category = Category.IMAGE;
        this.args.add(new OptionData(OptionType.USER, "user", "The pfp getting the rainbow treatment"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        String avatarUrl = (event.getOption("user") == null) ? event.getUser().getAvatarUrl() : Objects.requireNonNull(event.getOption("user")).getAsUser().getAvatarUrl();
        JSONObject data = postApiData(ImageManipulationAPI.IMAGE_API_URL, ImageManipulationAPI.makeRequestBody(avatarUrl, null, null, "rainbow", "image"));

        event.getHook().sendMessage(data.getString("url")).queue();
    }
}
