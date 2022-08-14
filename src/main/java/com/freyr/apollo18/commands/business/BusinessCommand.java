package com.freyr.apollo18.commands.business;

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
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.bson.Document;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BusinessCommand extends Command {

    public BusinessCommand(Apollo18 bot) {
        super(bot);
        this.name = "business";
        this.description = "Here you can find all of the business commands!";
        this.category = Category.BUSINESS;

        OptionData quantity = new OptionData(OptionType.INTEGER, "quantity", "The amount you want to buy").setMinValue(1);

        this.subCommands.add(new SubcommandData("market", "Shows all of the businesses"));
        this.subCommands.add(new SubcommandData("info", "Shows the info about a business").addOption(OptionType.STRING, "code", "The stock code", true));
        this.subCommands.add(new SubcommandData("buy", "Buy Stock").addOption(OptionType.STRING, "code", "The stock code of the business", true).addOptions(quantity));
        this.subCommands.add(new SubcommandData("sell", "Sell Stock").addOption(OptionType.STRING, "code", "The stock code of the business", true).addOptions(quantity));
        this.subCommands.add(new SubcommandData("jobs", "See the jobs of the business").addOption(OptionType.STRING, "code", "The stock code of the business", true));
        this.subCommands.add(new SubcommandData("set-job", "Set your job!").addOption(OptionType.STRING, "code", "The stock code of the business", true).addOption(OptionType.STRING, "job", "The name of your job", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        Database db = bot.getDatabase();

        switch (Objects.requireNonNull(event.getSubcommandName())) {
            case "market" -> {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Businesses");
                embed.setDescription("Here are all of the businesses that you can invest in!");

                for (Document business : db.getBusinesses()) {
                    embed.addField(business.getString("name"), "Price: <:byte:858172448900644874> `" + business.get("stock", Document.class).getInteger("currentPrice") + " bytes`" + business.get("stock", Document.class).getString("arrowEmoji") + "`(" + business.get("stock", Document.class).getInteger("change") + ")`\nCode: `" + business.getString("stockCode") + "`", true);
                }

                embed.setColor(EmbedColor.DEFAULT_COLOR);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
            }

            case "info" -> {
                String code = event.getOption("code").getAsString().toUpperCase();
                Document business = db.getBusiness(code);

                if (business == null) {
                    event.getHook().sendMessageEmbeds(EmbedUtils.createError(code + "'s business does not exist")).queue();
                    return;
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(EmbedColor.DEFAULT_COLOR);
                embed.setThumbnail(business.getString("logo"));
                embed.setTitle(business.getString("name"));
                embed.setDescription(business.getString("description"));
                embed.addField("Stock Info", "Price: <:byte:858172448900644874> `" + business.get("stock", Document.class).getInteger("currentPrice") + " bytes` " + business.get("stock", Document.class).getString("arrowEmoji") + " `(" + business.get("stock", Document.class).getInteger("change") + ")`\nCode: `" + business.getString("stockCode") + "`", false);

                event.getHook().sendMessageEmbeds(embed.build()).queue();
            }

            case "buy" -> {
                String code = event.getOption("code").getAsString().toUpperCase();
                int quantity = (event.getOption("quantity") != null) ? event.getOption("quantity").getAsInt() : 1;

                Document business = db.getBusiness(code);

                if (business == null) {
                    event.getHook().sendMessageEmbeds(EmbedUtils.createError(code + "'s business does not exist")).queue();
                    return;
                }

                if (db.getBalance(event.getUser().getId()) < (business.get("stock", Document.class).getInteger("currentPrice") * quantity)) {
                    event.getHook().sendMessageEmbeds(EmbedUtils.createError("You cannot afford this")).queue();
                    return;
                }

                db.addStockToUser(business, event.getUser().getId(), quantity);

                event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("Purchase Successful")).queue();
            }

            case "sell" -> {
                String code = event.getOption("code").getAsString().toUpperCase();
                int quantity = (event.getOption("quantity") != null) ? event.getOption("quantity").getAsInt() : 1;

                if (quantity > db.getTotalStocks(event.getUser().getId(), code)) {
                    event.getHook().sendMessageEmbeds(EmbedUtils.createError("You do not have enough stock to sell")).queue();
                    return;
                }

                if (db.getBusiness(code) == null) {
                    event.getHook().sendMessageEmbeds(EmbedUtils.createError(code + "'s business does not exist")).queue();
                    return;
                }

                db.removeStockFromUser(event.getUser().getId(), code, quantity);

                event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("Sale Successful")).queue();
            }

            case "jobs" -> {
                String code = event.getOption("code").getAsString().toUpperCase();
                Document business = db.getBusiness(code);
                if (business == null) {
                    event.getHook().sendMessageEmbeds(EmbedUtils.createError(code + "'s business does not exist")).queue();
                    return;
                }

                List<Document> jobs = db.getJobs(code);

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle(business.getString("name") + "'s Jobs");
                embed.setDescription("Here are the jobs from this business! Use `/business set-job` to set your job!");
                embed.setColor(EmbedColor.DEFAULT_COLOR);
                embed.setThumbnail(business.getString("logo"));

                for (int i = 0; i < jobs.size(); i++) {
                    if (jobs.get(i).getBoolean("available")) {
                        embed.addField((i + 1) + ") " + jobs.get(i).getString("name"),  jobs.get(i).getString("description") + "\nSalary: <:byte:858172448900644874> `" + jobs.get(i).getInteger("salary") + " bytes`", true);
                    }
                }

                event.getHook().sendMessageEmbeds(embed.build()).queue();
            }

            case "set-job" -> {
                String code = event.getOption("code").getAsString().toUpperCase();
                String job = event.getOption("job").getAsString().toLowerCase();
                Document business = db.getBusiness(code);
                if (business == null) {
                    event.getHook().sendMessageEmbeds(EmbedUtils.createError(code + "'s business does not exist")).queue();
                    return;
                }

                db.setJob(event.getUser().getId(), code, job);

                event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("Job has been set to __" + db.getJob(code, job).getString("name") + "__")).queue();
            }
        }
    }
}
