package com.freyr.apollo18.commands.economy;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.data.records.business.Business;
import com.freyr.apollo18.handlers.BusinessHandler;
import com.freyr.apollo18.util.embeds.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Objects;

public class BalanceCommand extends Command {

    public BalanceCommand(Apollo18 bot) {
        super(bot);
        this.name = "balance";
        this.description = "Check yours or another user's byte balance!";
        this.category = Category.ECONOMY;

        this.args.add(new OptionData(OptionType.USER, "user", "The bot will return this user's balance"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        Database db = bot.getDatabase();

        User user = (event.getOption("user") == null) ? event.getUser() : Objects.requireNonNull(event.getOption("user")).getAsUser();
        List<Business> businesses = db.getBusinesses();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(user.getEffectiveName() + "'s Balance");
        embed.setColor(EmbedColor.DEFAULT_COLOR);
        embed.setThumbnail(user.getAvatarUrl());
        embed.addField("Balance", BusinessHandler.byteEmoji + " " + db.getBalance(user.getId()) + " bytes", true);
        embed.addField("Bank", BusinessHandler.byteEmoji + " " + db.getBank(user.getId()) + " bytes", true);
        embed.addField("Net Worth", BusinessHandler.byteEmoji + " " + db.getNetWorth(user.getId()) + " bytes", true);
        embed.addField("Job", (db.getUserJob(user.getId()).jobName() == null) ? "None" : db.getUserJob(user.getId()).jobName(), false);
        for (Business business : businesses) {
            if (db.getTotalStocks(user.getId(), business.stockCode()) > 0) {
                embed.addField(business.name(), db.getTotalStocks(event.getUser().getId(), business.stockCode()) + " share(s)\nCode: `" + business.stockCode() + "`", true);
            }
        }

        event.getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
