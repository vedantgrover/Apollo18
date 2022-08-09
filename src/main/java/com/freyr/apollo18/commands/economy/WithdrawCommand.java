package com.freyr.apollo18.commands.economy;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class WithdrawCommand extends Command {

    public WithdrawCommand(Apollo18 bot) {
        super(bot);
        this.name = "withdraw";
        this.description = "Withdraw money from your bank!";
        this.category = Category.ECONOMY;

        this.args.add(new OptionData(OptionType.INTEGER, "amount", "Specify an amount to deposit").setMinValue(1));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        Database db = bot.getDatabase();
        OptionMapping amountOption = event.getOption("amount");
        int amount;

        if (amountOption == null) {
            amount = db.getBank(event.getUser().getId());
        } else {
            amount = amountOption.getAsInt();
        }

        if (amount == 0) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("You have no bytes to withdraw!")).queue();
            return;
        }

        if (amount < 0) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("You cannot withdraw negative bytes!")).queue();
            return;
        }

        if (amount > db.getBank(event.getUser().getId())) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("You don't have that many bytes!")).queue();
            return;
        }

        int oldBytes = db.getBank(event.getUser().getId());

        db.withdrawBytes(event.getUser().getId(), amount);
        event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("Withdrawn <:byte:858172448900644874> " + amount + " bytes")).queue();

        db.createTransaction(event.getUser().getId(), "Bank / Withdraw", oldBytes, db.getBank(event.getUser().getId()));
    }
}
