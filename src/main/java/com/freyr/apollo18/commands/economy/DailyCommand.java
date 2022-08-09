package com.freyr.apollo18.commands.economy;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class DailyCommand extends Command {

    public DailyCommand(Apollo18 bot) {
        super(bot);
        this.name = "daily";
        this.description = "Redeem your daily bytes!";
        this.category = Category.ECONOMY;
        this.cooldown = 86400;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        Database db = bot.getDatabase();

        int oldBal = db.getBalance(event.getUser().getId());

        int randBytes = (int) (Math.random() * (50 - 20) + 20);
        db.addBytes(event.getUser().getId(), randBytes);

        event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("Redeemed <:byte:858172448900644874> " + randBytes + " bytes")).queue();

        db.createTransaction(event.getUser().getId(), "Redemption / Daily", oldBal, db.getBalance(event.getUser().getId()));
    }
}
