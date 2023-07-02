package com.freyr.apollo18.commands.image;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.json.JSONObject;

import java.util.Objects;

/**
 * This command handles all the image manipulation using the Apollo API.
 */
public class ImageManipulationCommand extends Command {

    /**
     * Sets up the command parameters and adds the different manipulation choices.
     * @param bot
     */
    public ImageManipulationCommand(Apollo18 bot) {
        super(bot);

        this.name = "image";
        this.description = "Have fun with generating funny images!";
        this.category = Category.IMAGE;
        this.args.add(new OptionData(OptionType.STRING, "manipulation", "The manipulation you want", true).addChoice("beautiful", "beautiful").addChoice("trigger", "trigger").addChoice("distracted", "distracted").addChoice("facepalm", "facepalm").addChoice("fuse", "fuse").addChoice("hitler", "hitler").addChoice("invert", "invert").addChoice("jail", "jail").addChoice("joke over head", "jokeoverhead").addChoice("rainbow", "rainbow").addChoice("rip", "rip").addChoice("slap", "slap").addChoice("spank", "spank").addChoice("wanted", "wanted"));
        this.args.add(new OptionData(OptionType.USER, "user", "User you want in the picture"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        String manipulation = Objects.requireNonNull(event.getOption("manipulation")).getAsString(); // Gets the user requested manipulation

        User user = (event.getOption("user") == null) ? event.getUser() : Objects.requireNonNull(event.getOption("user")).getAsUser(); // Gets the user-specified user or defaults to author.
        User user2;

        JSONObject data;
        // Validates that two users are given if the manipulation needs two users.
        if (manipulation.equals("distracted") || manipulation.equals("fuse") || manipulation.equals("slap") || manipulation.equals("spank")) {
            if (event.getOption("user") == null) {
                event.getHook().sendMessageEmbeds(EmbedUtils.createError("Please add a second user for this manipulation")).queue();
                return;
            }

            user = event.getUser();
            user2 = Objects.requireNonNull(event.getOption("user")).getAsUser();

            // Sending post request with two image urls (avatar of both users)
            data = postApiData("http://apollo18.westus2.cloudapp.azure.com:3000/image", "{ \"url1\": \"" + user.getAvatarUrl() + "\", \"url2\": \"" + user2.getAvatarUrl() + "\", \"manipulation\": \"" + event.getOption("manipulation").getAsString() + "\"}");

        } else { // If image requires one user
            data = postApiData("http://apollo18.westus2.cloudapp.azure.com:3000/image", "{ \"url1\": \"" + user.getAvatarUrl() + "\", \"manipulation\": \"" + event.getOption("manipulation").getAsString() + "\"}");
        }

        event.getHook().sendMessage(data.getString("url")).queue();
    }
}
