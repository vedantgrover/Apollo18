package com.freyr.apollo18.commands.image_manipulation;

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

public class PixelateCommand extends Command {

    public PixelateCommand(Apollo18 bot) {
        super(bot);

        this.name = "pixelate";
        this.description = "Pixelate's the user's pfp";
        this.category = Category.IMAGE;

        this.args.add(new OptionData(OptionType.USER, "user", "The user's pfp you would like to pixelate"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        String avatarURL = (event.getOption("user") == null) ? event.getUser().getAvatarUrl() : Objects.requireNonNull(event.getOption("user")).getAsUser().getAvatarUrl();

        try {
            assert avatarURL != null;
            File invertedImage = JPixel.pixelate(avatarURL);

            FileUpload fileUpload = FileUpload.fromData(invertedImage);
            event.getHook().sendFiles(fileUpload).queue();
            invertedImage.delete();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
