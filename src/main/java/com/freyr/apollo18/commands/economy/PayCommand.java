package com.freyr.apollo18.commands.economy;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.handlers.BusinessHandler;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Objects;

public class PayCommand extends Command {

    public PayCommand(Apollo18 bot) {
        super(bot);
        this.name = "pay";
        this.description = "Pay users with bytes!";
        this.category = Category.ECONOMY;

        this.args.add(new OptionData(OptionType.USER, "user", "The person you want to pay", true));
        this.args.add(new OptionData(OptionType.INTEGER, "bytes", "The amount of bytes you want to pay", true).setMinValue(1));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        Database db = bot.getDatabase();
        User user = event.getOption("user").getAsUser();
        int bytes = event.getOption("bytes").getAsInt();

        if (db.getBalance(event.getUser().getId()) < bytes) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("You do not have enough bytes")).queue();
            return;
        }

        int giverOldBal = db.getBalance(event.getUser().getId());
        int receiverOldBal = db.getBalance(user.getId());

        db.addBytes(user.getId(), bytes);
        db.removeBytes(event.getUser().getId(), bytes);

        event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("Payed " + user.getName() + " " + BusinessHandler.byteEmoji + " " + bytes + " bytes!")).queue();

        if (db.getNotificationToggle(user.getId())) {
            Objects.requireNonNull(event.getJDA().getUserById(user.getId())).openPrivateChannel().flatMap(channel -> channel.sendMessageEmbeds(EmbedUtils.createNotification(event.getUser().getName() + " has just payed you " + BusinessHandler.byteEmoji + " " + bytes + " bytes!"))).queue();
        }

        db.createTransaction(event.getUser().getId(), "Payment / Pay", giverOldBal, db.getBalance(event.getUser().getId()));
        db.createTransaction(user.getId(), "Payment / Receive", receiverOldBal, db.getBalance(user.getId()));
    }
}
