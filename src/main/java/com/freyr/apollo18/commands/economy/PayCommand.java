package com.freyr.apollo18.commands.economy;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class PayCommand extends Command {

    public PayCommand(Apollo18 bot) {
        super(bot);
        this.name = "pay";
        this.description = "Pay users with bytes!";
        this.category = Category.ECONOMY;

        this.args.add(new OptionData(OptionType.USER, "user", "The person you want to pay", true));
        this.args.add(new OptionData(OptionType.INTEGER, "bytes", "The amount of bytes you want to pay", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        Database db = bot.getDatabase();
        User user = event.getOption("user").getAsUser();
        int bytes = event.getOption("bytes").getAsInt();

        db.addBytes(user.getId(), bytes);
        db.removeBytes(event.getUser().getId(), bytes);

        event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("Payed " + user.getName() + " <:byte:858172448900644874> " + bytes + " bytes!")).queue();
    }
}
