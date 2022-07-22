package com.freyr.apollo18.commands.settings;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Objects;

public class LevelingSettings extends Command {

    public LevelingSettings(Apollo18 bot) {
        super(bot);
        this.name = "leveling-settings";
        this.description = "Change the leveling settings within your server";
        this.category = Category.SETTINGS;
        this.permission = Permission.ADMINISTRATOR;

        OptionData channelOption = new OptionData(OptionType.CHANNEL, "channel", "The channel you would like leveling messages to be in.", true).setChannelTypes(ChannelType.TEXT);

        this.subCommands.add(new SubcommandData("toggle", "Turn leveling on or off for your server."));
        this.subCommands.add(new SubcommandData("set-channel", "Set a channel for leveling messages.").addOptions(channelOption));
        this.subCommands.add(new SubcommandData("set-message", "Sets the message sent when a user levels up.").addOption(OptionType.STRING, "message", "Use [member] for the member name and [level] for the level.", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        Database db = bot.getDatabase();

        String subCommand = event.getSubcommandName();

        switch (Objects.requireNonNull(subCommand)) {
            case "toggle" -> {
                db.toggleLevelingSystem(event.getGuild().getIdLong());
                event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("The leveling system has been __" + ((db.getLevelingSystemToggle(event.getGuild().getIdLong())) ? "enabled":"disabled") + "__")).queue();
            }

            case "set-channel" -> {
                MessageChannel channel = (MessageChannel) event.getOption("channel").getAsChannel();
                db.setLevelingChannel(event.getGuild().getIdLong(), channel.getIdLong());

                event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("The leveling channel has been set to " + channel.getAsMention())).queue();
            }

            case "set-message" -> {
                String message = event.getOption("message").getAsString();
                db.setLevelingMessage(event.getGuild().getIdLong(), message);

                event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("Leveling message has been set.")).queue();
            }
        }
    }
}
