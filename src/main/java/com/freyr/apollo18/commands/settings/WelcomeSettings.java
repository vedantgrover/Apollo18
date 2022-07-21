package com.freyr.apollo18.commands.settings;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Objects;

public class WelcomeSettings extends Command {

    public WelcomeSettings(Apollo18 bot) {
        super(bot);
        this.name = "welcome-settings";
        this.description = "Change the welcome settings of your bot";
        this.category = Category.SETTINGS;

        this.permission = Permission.ADMINISTRATOR;

        OptionData channelOption = new OptionData(OptionType.CHANNEL, "channel", "The channel you want messages to go into", true).setChannelTypes(ChannelType.TEXT);
        OptionData memberCountChannelOption = new OptionData(OptionType.CHANNEL, "channel", "The channel you want member count to happen in.", true).setChannelTypes(ChannelType.VOICE);

        this.subCommands.add(new SubcommandData("toggle", "Turn the welcome system on or off"));
        this.subCommands.add(new SubcommandData("set-welcome-channel", "Set the channel you want welcome messages to go into.").addOptions(channelOption));
        this.subCommands.add(new SubcommandData("set-welcome-message", "Set the welcome message for your server.").addOption(OptionType.STRING, "message", "Use [member] for the joining member's name and [server] for your server name.", true));
        this.subCommands.add(new SubcommandData("set-leave-channel", "Set the channel you want leave messages to go into.").addOptions(channelOption));
        this.subCommands.add(new SubcommandData("set-leave-message", "Set the leave message for your server.").addOption(OptionType.STRING, "message", "Use [member] for the leaving member's name and [server] for your server name.", true));
        this.subCommands.add(new SubcommandData("set-membercount-channel", "Set the channel you want member count to happen in").addOptions(memberCountChannelOption));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        Database db = bot.getDatabase();

        String subCommand = event.getSubcommandName();

        switch (Objects.requireNonNull(subCommand)) {
            case "toggle" -> {
                db.toggleWelcomeSystem(event.getGuild().getIdLong());
                event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("The Welcome System has been __" + (db.getWelcomeSystemToggle(event.getGuild().getIdLong()) ? "enabled":"disabled") + "__")).queue();
            }

            case "set-welcome-channel" -> {
                if (!db.getWelcomeSystemToggle(event.getGuild().getIdLong())) {
                    event.getHook().sendMessageEmbeds(EmbedUtils.createError("You have not turned on the Welcome System yet. Please use `/welcome-settings toggle` to turn it on.")).queue();
                    return;
                }

                GuildChannel channel = event.getOption("channel").getAsChannel();
                db.setWelcomeChannel(event.getGuild().getIdLong(), channel.getIdLong());

                event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("The Welcome Channel has been set to " + channel.getAsMention())).queue();
            }

            case "set-welcome-message" -> {
                if (!db.getWelcomeSystemToggle(event.getGuild().getIdLong())) {
                    event.getHook().sendMessageEmbeds(EmbedUtils.createError("You have not turned on the Welcome System yet. Please use `/welcome-settings toggle` to turn it on.")).queue();
                    return;
                }

                String welcomeMessage = event.getOption("message").getAsString();
                db.setWelcomeMessage(event.getGuild().getIdLong(), welcomeMessage);

                event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("Welcome Message Successfully set")).queue();
            }

            case "set-leave-channel" -> {
                if (!db.getWelcomeSystemToggle(event.getGuild().getIdLong())) {
                    event.getHook().sendMessageEmbeds(EmbedUtils.createError("You have not turned on the Welcome System yet. Please use `/welcome-settings toggle` to turn it on.")).queue();
                    return;
                }

                GuildChannel channel = event.getOption("channel").getAsChannel();
                db.setLeaveChannel(event.getGuild().getIdLong(), channel.getIdLong());

                event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("The Leave Channel has been set to " + channel.getAsMention())).queue();
            }

            case "set-leave-message" -> {
                if (!db.getWelcomeSystemToggle(event.getGuild().getIdLong())) {
                    event.getHook().sendMessageEmbeds(EmbedUtils.createError("You have not turned on the Welcome System yet. Please use `/welcome-settings toggle` to turn it on.")).queue();
                    return;
                }

                String welcomeMessage = event.getOption("message").getAsString();
                db.setLeaveMessage(event.getGuild().getIdLong(), welcomeMessage);

                event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("Leave Message Successfully set")).queue();
            }

            case "set-membercount-channel" -> {
                if (!db.getWelcomeSystemToggle(event.getGuild().getIdLong())) {
                    event.getHook().sendMessageEmbeds(EmbedUtils.createError("You have not turned on the Welcome System yet. Please use `/welcome-settings toggle` to turn it on.")).queue();
                    return;
                }

                GuildChannel channel = event.getOption("channel").getAsChannel();
                db.setMemberCountChannel(event.getGuild().getIdLong(), channel.getIdLong());

                event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("The Member Count Channel has been set to " + channel.getAsMention())).queue();
            }
        }
    }
}
