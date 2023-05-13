package com.freyr.apollo18.commands.economy;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.handlers.BusinessHandler;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DailyCommand extends Command {

    public DailyCommand(Apollo18 bot) {
        super(bot);
        this.name = "daily";
        this.description = "Redeem your daily bytes!";
        this.category = Category.ECONOMY;

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Los_Angeles"));
        ZonedDateTime nextRun = now.withHour(1).withMinute(0).withSecond(0);
        if(now.compareTo(nextRun) > 0) {
            nextRun = nextRun.plusDays(1);
        }

        Duration duration = Duration.between(now, nextRun);
        long initialDelay = duration.getSeconds();
        System.out.println("Will run in: " + initialDelay);

        this.cooldown = Integer.parseInt(Long.toString(initialDelay));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        Database db = bot.getDatabase();

        int oldBal = db.getBalance(event.getUser().getId());

        int randBytes = (int) (Math.random() * (50 - 20) + 20);
        db.addBytes(event.getUser().getId(), randBytes);

        event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("Redeemed " + BusinessHandler.byteEmoji + " " + randBytes + " bytes")).queue();

        db.createTransaction(event.getUser().getId(), "Redemption / Daily", oldBal, db.getBalance(event.getUser().getId()));
    }
}
