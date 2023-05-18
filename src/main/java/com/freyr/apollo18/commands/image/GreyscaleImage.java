package com.freyr.apollo18.commands.image;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import io.github.vedantgrover.JPixel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class GreyscaleImage extends Command {

    public GreyscaleImage(Apollo18 bot) {
        super(bot);

        this.name = "greyscale";
        this.description = "Greyscales the user's pfp";
        this.category = Category.IMAGE;

        this.args.add(new OptionData(OptionType.USER, "user", "The user's pfp you would like to greyscale"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        String avatarURL = (event.getOption("user") == null) ? event.getUser().getAvatarUrl() : Objects.requireNonNull(event.getOption("user")).getAsUser().getAvatarUrl();

        try {
            assert avatarURL != null;
            File invertedImage = JPixel.greyScaleImage(avatarURL);

            FileUpload fileUpload = FileUpload.fromData(invertedImage);
            event.getHook().sendFiles(fileUpload).queue();
            invertedImage.delete();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
