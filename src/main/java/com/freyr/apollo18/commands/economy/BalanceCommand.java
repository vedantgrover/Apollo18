package com.freyr.apollo18.commands.economy;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.util.embeds.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bson.Document;

import javax.print.Doc;
import java.util.List;

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

        User user = (event.getOption("user") == null) ? event.getUser() : event.getOption("user").getAsUser();
        List<Document> businesses = db.getBusinesses();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(user.getName() + "'s Balance");
        embed.setColor(EmbedColor.DEFAULT_COLOR);
        embed.setThumbnail(user.getAvatarUrl());
        embed.addField("Balance", "<:byte:858172448900644874> " + db.getBalance(user.getId()) + " bytes", true);
        embed.addField("Bank", "<:byte:858172448900644874> " + db.getBank(user.getId()) + " bytes", true);
        embed.addField("Net Worth", "<:byte:858172448900644874> " + db.getNetWorth(user.getId()) + " bytes", true);

        for (int i = 0; i < businesses.size(); i++) {
            if (db.getTotalStocks(user.getId(), businesses.get(i).getString("stockCode")) > 0) {
                embed.addField(businesses.get(i).getString("name"), db.getTotalStocks(event.getUser().getId(), businesses.get(i).getString("stockCode")) + " share(s)", true);
            }
        }


        event.getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
