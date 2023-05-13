package com.freyr.apollo18.commands.fun;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Objects;

public class AvatarCommand extends Command {

    public AvatarCommand(Apollo18 bot) {
        super(bot);
        this.name = "avatar";
        this.description = "Returns the avatar (or pfp) of a user";
        this.category = Category.FUN;

        this.args.add(new OptionData(OptionType.USER, "user", "This user's avatar will be returned"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        User user = (event.getOption("user") == null) ? event.getUser() : Objects.requireNonNull(event.getOption("user")).getAsUser();

        event.getHook().sendMessage(Objects.requireNonNull(user.getAvatarUrl())).queue();
    }
}
