package com.freyr.apollo18.commands.leveling;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.handlers.LevelingHandler;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class RankCommand extends Command {

    public RankCommand(Apollo18 bot) {
        super(bot);
        this.name = "rank";
        this.description = "Check your server rank";
        this.category = Category.LEVELING;

        this.args.add(new OptionData(OptionType.USER, "user", "This user's rank data"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        Database db = bot.getDatabase();

        User user = (event.getOption("user") == null) ? event.getUser():event.getOption("user").getAsUser();

        try {
            EmbedBuilder embed = new EmbedBuilder();

            embed.setColor(EmbedColor.DEFAULT_COLOR);
            embed.setThumbnail(user.getAvatarUrl());
            embed.setTitle(user.getName() + "'s Rank");
            embed.setDescription("**__Level " + db.getUserLevelingProfile(user.getId(), event.getGuild().getId()).getInteger("level") + "__**");
            embed.addField("Xp", db.getUserLevelingProfile(user.getId(), event.getGuild().getId()).getInteger("xp") + " / " + LevelingHandler.calculateLevelGoal(db.getUserLevelingProfile(user.getId(), event.getGuild().getId()).getInteger("level")), false);

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        } catch (Exception e) {
            e.printStackTrace();
            event.getHook().sendMessageEmbeds(EmbedUtils.createError(user.getName() + " has not sent any messages within this server.")).queue();
        }
    }
}
