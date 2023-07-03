package com.freyr.apollo18.commands.image;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.api.ImageManipulationAPI;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.json.JSONObject;

import java.util.Objects;

public class TriggerCommand extends Command {

    public TriggerCommand(Apollo18 bot) {
        super(bot);

        this.name = "trigger";
        this.description = "Applies the trigger gif over an image";
        this.category = Category.IMAGE;
        this.args.add(new OptionData(OptionType.USER, "user", "Applies the gif over a user's pfp"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        String avatarUrl = (event.getOption("user") == null) ? event.getUser().getAvatarUrl() : Objects.requireNonNull(event.getOption("user")).getAsUser().getAvatarUrl();
        JSONObject data = postApiData(ImageManipulationAPI.API_URL, ImageManipulationAPI.makeRequestBody(avatarUrl, null, "trigger"));

        event.getHook().sendMessage(data.getString("url")).queue();
    }
}
