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

public class FuseCommand extends Command {

    public FuseCommand(Apollo18 bot) {
        super(bot);

        this.name = "fuse";
        this.description = "Lets you fuse your pfp with someone else";
        this.category = Category.IMAGE;
        this.args.add(new OptionData(OptionType.USER, "user", "The other pfp", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        JSONObject data = postApiData(ImageManipulationAPI.API_URL, ImageManipulationAPI.makeRequestBody(event.getUser().getAvatarUrl(), Objects.requireNonNull(event.getOption("user")).getAsUser().getAvatarUrl(), "fuse"));

        event.getHook().sendMessage(data.getString("url")).queue();
    }
}
