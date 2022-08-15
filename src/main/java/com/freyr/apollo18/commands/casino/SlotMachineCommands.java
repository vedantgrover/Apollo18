package com.freyr.apollo18.commands.casino;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import jdk.jfr.DataAmount;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.List;

public class SlotMachineCommands extends Command {

    private final List<String> emojis = Arrays.asList("\uD83C\uDF4E", "\uD83E\uDD51", "\uD83C\uDF4C", "\uD83C\uDF52", "\uD83C\uDF47", "\uD83C\uDF4F", "\uD83E\uDD5D", "\uD83C\uDF4B", "\uD83E\uDD6D", "\uD83C\uDF48");

    public SlotMachineCommands(Apollo18 bot) {
        super(bot);
        this.name = "slots";
        this.description = "Try out the slot machine and you might get 20x what you bet!";
        this.category = Category.CASINO;

        this.args.add(new OptionData(OptionType.INTEGER, "bet", "Put in a bet!").setMinValue(1));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        Database db = bot.getDatabase();
        int bet = (event.getOption("bet") != null) ? event.getOption("bet").getAsInt() : 0;

        String firstChoice = emojis.get((int) (Math.random() * emojis.size()));
        String secondChoice = emojis.get((int) (Math.random() * emojis.size()));
        String thirdChoice = emojis.get((int) (Math.random() * emojis.size()));

        String text = "▪️ " + emojis.get((int) (Math.random() * emojis.size())) + " : " + emojis.get((int) (Math.random() * emojis.size())) + " : " + emojis.get((int) (Math.random() * emojis.size())) + "\n➡️ " + firstChoice + " : " + secondChoice + " : " + thirdChoice + " ⬅️ \n▪️ " + emojis.get((int) (Math.random() * emojis.size())) + " : " + emojis.get((int) (Math.random() * emojis.size())) + " : " + emojis.get((int) (Math.random() * emojis.size()));

        int oldBal = db.getBalance(event.getUser().getId());

        if (bet > db.getBalance(event.getUser().getId())) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("You do not have enough money in your wallet")).queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        if (firstChoice.equals(secondChoice) && secondChoice.equals(thirdChoice)) {
            db.addBytes(event.getUser().getId(), bet * 20);

            embed.setTitle("Success");
            embed.setDescription(text);
            embed.addField("Winnings", "You won <:byte:858172448900644874> " + (bet * 20) + " bytes", false);
            embed.setColor(EmbedColor.DEFAULT_COLOR);
        } else {
            db.removeBytes(event.getUser().getId(), bet);

            embed.setTitle("Fail");
            embed.setDescription(text);
            embed.addField("Losses", "You lost **<:byte:858172448900644874> " + bet + " bytes**", false);
            embed.setColor(EmbedColor.DEFAULT_COLOR);
        }

        event.getHook().sendMessageEmbeds(embed.build()).queue();

        db.createTransaction(event.getUser().getId(), "Casino / Slots", oldBal, db.getBalance(event.getUser().getId()));
    }
}
