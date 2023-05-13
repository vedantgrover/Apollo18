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
import java.net.URL;
import java.util.Objects;

public class InvertCommand extends Command {

    public InvertCommand(Apollo18 bot) {
        super(bot);

        this.name = "invert";
        this.description = "Returns the avatar (or pfp) of a user";
        this.category = Category.IMAGE;

        this.args.add(new OptionData(OptionType.USER, "user", "The user's pfp you would like to invert"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        String avatarURL = (event.getOption("user") == null) ? event.getUser().getAvatarUrl() : Objects.requireNonNull(event.getOption("user")).getAsUser().getAvatarUrl();

        try {
            assert avatarURL != null;
            File invertedImage = JPixel.invertImage(avatarURL);

            FileUpload fileUpload = FileUpload.fromData(invertedImage);
            event.getHook().sendFiles(fileUpload).queue();
            invertedImage.delete();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
