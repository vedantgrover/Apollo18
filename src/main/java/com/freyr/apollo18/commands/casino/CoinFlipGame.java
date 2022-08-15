package com.freyr.apollo18.commands.casino;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Random;

public class CoinFlipGame extends Command {

    public CoinFlipGame(Apollo18 bot) {
        super(bot);
        this.name = "coin-flip";
        this.description = "Put in a bet and try guessing the coin flip!";
        this.category = Category.CASINO;

        OptionData coinChoice = new OptionData(OptionType.STRING, "guess", "Pick between heads or tails!", true);
        coinChoice.addChoice("heads", "heads");
        coinChoice.addChoice("tails", "tails");

        this.args.add(coinChoice);
        this.args.add(new OptionData(OptionType.INTEGER, "bet", "Pick an amount to bet!").setMinValue(1));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        Random rd = new Random();
        Database db = bot.getDatabase();

        int bet = (event.getOption("bet") != null) ? event.getOption("bet").getAsInt() : 0;

        if (bet > db.getBalance(event.getUser().getId())) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("You do not have enough money in your wallet")).queue();
            return;
        }

        boolean flip = rd.nextBoolean();
        boolean choice = event.getOption("guess").getAsString().equals("heads");

        int oldBal = db.getBalance(event.getUser().getId());

        if (choice == flip) {
            db.addBytes(event.getUser().getId(), bet);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Success");
            embed.setColor(EmbedColor.DEFAULT_COLOR);
            embed.addField("The coin landed " + ((flip) ? "heads" : "tails"), ":white_check_mark: - You won <:byte:858172448900644874> **" + bet + "** bytes", false);
            embed.setThumbnail("https://c.tenor.com/cZK8lK6_xbIAAAAM/bravo-clap.gif");

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        } else {
            db.removeBytes(event.getUser().getId(), bet);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Fail");
            embed.setColor(EmbedColor.DEFAULT_COLOR);
            embed.addField("The coin landed " + ((flip) ? "heads" : "tails"), ":x: - You lost <:byte:858172448900644874> **" + bet + "** bytes", false);
            embed.setThumbnail("https://media.tenor.com/images/d9e906c20d34be7123762250fc39dcd6/tenor.gif");

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }

        db.createTransaction(event.getUser().getId(), "Casino / Coin-flip", oldBal, db.getBalance(event.getUser().getId()));
    }
}
