package com.freyr.apollo18.commands.casino;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CrashGame extends Command {

    private final Database db = bot.getDatabase();

    public CrashGame(Apollo18 bot) {
        super(bot);
        this.name = "crash";
        this.description = "Multiply your bytes and cash in before it crashes";
        this.category = Category.CASINO;
        this.args.add(new OptionData(OptionType.INTEGER, "bet", "The starting money you want to multiply", true).setMinValue(1));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        if (event.getOption("bet").getAsInt() > db.getBalance(event.getUser().getId())) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("You do not have enough money in your wallet")).queue();
            return;
        }

        Crash crash = new Crash(event.getOption("bet").getAsInt(), event.getUser().getId());
        Button crashButton = Button.of(ButtonStyle.SECONDARY, "cash", "Cash");

        event.getHook().sendMessageEmbeds(new EmbedBuilder().setTitle("Crash").setColor(EmbedColor.DEFAULT_COLOR).setDescription("Current Bet: **" + event.getOption("bet").getAsString() + "bytes**").addField("Multiplier", "1.0x", true).addField("Crash Value", event.getOption("bet").getAsString() + " bytes", true).build()

        ).addActionRow(crashButton).queue(message -> {
            if (crash.startGame(message, crashButton)) {
                db.removeBytes(event.getUser().getId(), crash.startingBal);
                System.out.println(event.getUser().getName() + " CRASHED");
            } else {
                db.addBytes(event.getUser().getId(), (int) (crash.startingBal * crash.currentMultiplier));
            }
        });
    }

    public static class Crash {
        private int startingBal;
        private double currentMultiplier;
        private int crashAfterIterations;
        private int currentIteration;
        private boolean crashed;
        private static boolean cashIn;

        private static String userId;

        public Crash(int startingBal, String userId) {
            this.startingBal = startingBal;
            currentMultiplier = 1.0;
            this.crashAfterIterations = (int) (Math.random() * (30 - 5)) + 5;
            System.out.println(crashAfterIterations);
            this.currentIteration = 0;
            crashed = false;
            cashIn = false;
            Crash.userId = userId;
        }

        private boolean startGame(Message message, Button cashButton) {
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(() -> {
                currentMultiplier += 0.1;
                currentIteration += 1;
                message.editMessageEmbeds(new EmbedBuilder().setTitle("Crash").setColor(EmbedColor.DEFAULT_COLOR).setDescription("Current Bet: **" + startingBal + " bytes**").addField("Multiplier", new DecimalFormat("#.##").format(currentMultiplier) + "x", true).addField("Crash Value", (int) (currentMultiplier * startingBal) + " bytes", true).build()).queue();
                if (currentIteration == crashAfterIterations) {
                    message.editMessageEmbeds(new EmbedBuilder().setTitle("Crash").setColor(EmbedColor.ERROR_COLOR).setDescription("Current Bet: **" + startingBal + " bytes**").addField("Multiplier", new DecimalFormat("#.##").format(currentMultiplier) + "x", true).addField("Crash Value", "-" + startingBal + " bytes", true).build()).setActionRow(cashButton.asDisabled()).queue();
                    crashed = true;
                    executor.shutdown();
                }
                if (cashIn) {
                    message.editMessageEmbeds(new EmbedBuilder().setTitle("You Won!").setColor(EmbedColor.DEFAULT_COLOR).setDescription("Winnings: **" + (int) (startingBal * currentMultiplier) + " bytes**").addField("Multiplier", new DecimalFormat("#.##").format(currentMultiplier) + "x", true).build()).setActionRow(cashButton.asDisabled()).queue();
                    executor.shutdown();
                }
            }, 0, 2, TimeUnit.SECONDS);
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return crashed;
        }

        public static void cashIn() {
            cashIn = true;
        }

        public static String getUserID() {
            return userId;
        }
    }
}
