package com.freyr.apollo18.commands.business;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.handlers.BusinessHandler;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.FileUpload;
import org.bson.Document;

import java.io.File;
import java.util.List;
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

        EmbedBuilder embed;
        String code;
        Document business;

        switch (Objects.requireNonNull(event.getSubcommandName())) {
            case "market":
                embed = new EmbedBuilder();
                embed.setTitle("Businesses");
                embed.setDescription("Here are all of the businesses that you can invest in!");

                for (Document currentBusiness : db.getBusinesses()) {
                    embed.addField(currentBusiness.getString("name"), "Price: " + BusinessHandler.byteEmoji + "`" + currentBusiness.get("stock", Document.class).getInteger("currentPrice") + " bytes`" + currentBusiness.get("stock", Document.class).getString("arrowEmoji") + "\nChange: `" + currentBusiness.get("stock", Document.class).getInteger("change") + " bytes`\nCode: `" + currentBusiness.getString("stockCode") + "`", true);
                }

                embed.setColor(EmbedColor.DEFAULT_COLOR);
                event.getHook().sendMessageEmbeds(embed.build()).queue();
                break;

            case "info":
                code = Objects.requireNonNull(event.getOption("code")).getAsString().toUpperCase();
                business = db.getBusiness(code);

                if (business == null) {
                    event.getHook().sendMessageEmbeds(EmbedUtils.createError(code + "'s business does not exist")).queue();
                    return;
                }

                embed = new EmbedBuilder();
                embed.setColor(EmbedColor.DEFAULT_COLOR);
                embed.setThumbnail(business.getString("logo"));
                embed.setTitle(business.getString("name"));
                embed.setDescription(business.getString("description"));
                embed.addField("Stock Info", "Price: " + BusinessHandler.byteEmoji + " `" + business.get("stock", Document.class).getInteger("currentPrice") + " bytes` " + business.get("stock", Document.class).getString("arrowEmoji") + " `(" + business.get("stock", Document.class).getInteger("change") + ")`\nCode: `" + business.getString("stockCode") + "`", false);

                String imagePath = "src/main/resources/stock_data/" + business.get("stock", Document.class).getString("ticker") + "/" + business.get("stock", Document.class).getString("ticker") + "-graph.png";
                System.out.println(imagePath);

                File graph = new File(imagePath);
                embed.setImage("attachment://graph.png");

                event.getHook().sendMessageEmbeds(embed.build()).addFiles(FileUpload.fromData(graph, "graph.png")).queue();
                break;

            case "buy":
                code = Objects.requireNonNull(event.getOption("code")).getAsString().toUpperCase();
                int quantity = (event.getOption("quantity") != null) ? Objects.requireNonNull(event.getOption("quantity")).getAsInt() : 1;

                business = db.getBusiness(code);

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
                break;

            case "sell":
                code = Objects.requireNonNull(event.getOption("code")).getAsString().toUpperCase();
                quantity = (event.getOption("quantity") != null) ? Objects.requireNonNull(event.getOption("quantity")).getAsInt() : 1;

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
                break;

            case "jobs":
                code = Objects.requireNonNull(event.getOption("code")).getAsString().toUpperCase();
                business = db.getBusiness(code);
                if (business == null) {
                    event.getHook().sendMessageEmbeds(EmbedUtils.createError(code + "'s business does not exist")).queue();
                    return;
                }

                List<Document> jobs = db.getJobs(code);

                embed = new EmbedBuilder();
                embed.setTitle(business.getString("name") + "'s Jobs");
                embed.setDescription("Here are the jobs from this business! Use `/business set-job` to set your job!");
                embed.setColor(EmbedColor.DEFAULT_COLOR);
                embed.setThumbnail(business.getString("logo"));

                for (int i = 0; i < jobs.size(); i++) {
                    if (jobs.get(i).getBoolean("available")) {
                        embed.addField((i + 1) + ") " + jobs.get(i).getString("name"), jobs.get(i).getString("description") + "\nSalary: " + BusinessHandler.byteEmoji + " `" + jobs.get(i).getInteger("salary") + " bytes`", true);
                    }
                }

                event.getHook().sendMessageEmbeds(embed.build()).queue();
                break;

            case "set-job":
                code = Objects.requireNonNull(event.getOption("code")).getAsString().toUpperCase();
                String job = Objects.requireNonNull(event.getOption("job")).getAsString().toLowerCase();
                business = db.getBusiness(code);
                if (business == null) {
                    event.getHook().sendMessageEmbeds(EmbedUtils.createError(code + "'s business does not exist")).queue();
                    return;
                }

                if (db.getJob(code, job) == null) {
                    event.getHook().sendMessageEmbeds(EmbedUtils.createError("That job doesn't exist in that company!")).queue();
                    return;
                }

                event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("Job has been set to __" + db.getJob(code, job).getString("name") + "__")).queue();
                break;
        }
    }
}
