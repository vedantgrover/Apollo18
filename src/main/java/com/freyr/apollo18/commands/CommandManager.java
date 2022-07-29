package com.freyr.apollo18.commands;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.dev.CreateGuild;
import com.freyr.apollo18.commands.dev.CreateProfile;
import com.freyr.apollo18.commands.dev.CreateUser;
import com.freyr.apollo18.commands.economy.BalanceCommand;
import com.freyr.apollo18.commands.economy.DepositCommand;
import com.freyr.apollo18.commands.economy.WithdrawCommand;
import com.freyr.apollo18.commands.fun.AvatarCommand;
import com.freyr.apollo18.commands.fun.EmoteCommand;
import com.freyr.apollo18.commands.fun.MemeCommand;
import com.freyr.apollo18.commands.information.*;
import com.freyr.apollo18.commands.leveling.LeaderboardCommand;
import com.freyr.apollo18.commands.leveling.RankCommand;
import com.freyr.apollo18.commands.music.*;
import com.freyr.apollo18.commands.settings.LevelingSettings;
import com.freyr.apollo18.commands.settings.WelcomeSettings;
import com.freyr.apollo18.commands.utility.*;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class will handle all the commands that I make and add them into Discord.
 * <p>
 * Guild Commands - These commands can be added per guild and are only available within that guild. (Max: 100)
 * Global Commands - These commands are available across all servers. It takes up to an hour sometimes more to register. (Max: unlimited)
 *
 * @author Freyr
 */
public class CommandManager extends ListenerAdapter {

    public static final List<Command> commands = new ArrayList<>(); // Contains all the commands

    public static final Map<String, Command> mapCommands = new HashMap<>(); // Contains all the commands with their identifiers (names)

    private final Apollo18 bot;

    public CommandManager(Apollo18 bot) {
        this.bot = bot;
        mapCommands(
                // Utility Commands
                new PingCommand(bot), new InviteCommand(bot), new ReportBugCommand(bot), new SuggestCommand(bot), new VoteCommand(bot), new PollCommand(bot), new MathCommand(bot),
                // Music Commands
                new PlayCommand(bot), new StopCommand(bot), new SkipCommand(bot), new NowPlayingCommand(bot), new QueueCommand(bot), new LoopCommand(bot), new VolumeCommand(bot), new PauseCommand(bot), new ResumeCommand(bot), new PlaylistCommand(bot),
                // Information Commands
                new WeatherCommand(bot), new YouTubeCommand(bot), new TwitterCommand(bot), new ServerInfoCommand(bot), new UserInfoCommand(bot), new TranslateCommand(bot),

                // Fun Commands
                new MemeCommand(bot), new EmoteCommand(bot), new AvatarCommand(bot),

                // Leveling Commands
                new RankCommand(bot), new LeaderboardCommand(bot),

                // Economy Commands
                new BalanceCommand(bot),
                new DepositCommand(bot),
                new WithdrawCommand(bot),

                // Settings Commands
                new WelcomeSettings(bot), new LevelingSettings(bot),

                // Dev only
                new CreateProfile(bot), new CreateUser(bot), new CreateGuild(bot),

                // Help command should come at the bottom
                new HelpCommand(bot));
    }

    /**
     * Adds the commands into the map and the arraylist
     *
     * @param cmds All the commands you want the bot to execute
     */
    private void mapCommands(Command... cmds) {
        for (Command cmd : cmds) {
            mapCommands.put(cmd.name, cmd);
            commands.add(cmd);
        }
    }

    /**
     * Creates CommandData for each command which is used to add the command into discord
     *
     * @return A list of command data for the Discord API to go through and add into Discord
     */
    public List<CommandData> unpackCommandData() {
        List<CommandData> commandData = new ArrayList<>();
        for (Command cmd : commands) {
            CommandData data = Commands.slash(cmd.name, cmd.description).addOptions(cmd.args).addSubcommands(cmd.subCommands); // Creating a new slash command with the properties located within the command
            if (cmd.permission != null) {
                data.setDefaultPermissions(DefaultMemberPermissions.enabledFor(cmd.permission));
            }
            commandData.add(data);
        }

        return commandData;
    }

    /**
     * This method fires everytime someone uses a slash command.
     *
     * @param event Has all the information about the event.
     */
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        bot.getDatabase().createUserData(event.getUser());
        Command cmd = mapCommands.get(event.getName()); // Getting the command based off of the name received in the event
        if (cmd != null) {
            if (cmd.devOnly && !event.getUser().getId().equals("622506118551437322")) {
                event.replyEmbeds(EmbedUtils.createError("This is a **developer only** command")).setEphemeral(true).queue();
                return;
            }
            Role botRole = event.getGuild().getBotRole();
            if (cmd.botPermission != null) {
                if (!botRole.hasPermission(cmd.botPermission) && !botRole.hasPermission(Permission.ADMINISTRATOR)) {
                    String text = "I need the `" + cmd.botPermission.getName().toUpperCase() + "` permission to execute that command.";
                    event.replyEmbeds(EmbedUtils.createError(text)).setEphemeral(true).queue();
                }
            }
            cmd.execute(event); // Executing the execute method.
        }
    }

    /**
     * This method fires everytime the guild has been loaded up for the bot.
     * I will be using this method mostly for testing purposes (Creating guild commands and testing)
     *
     * @param event Has all the information about the event.
     */
    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        event.getGuild().updateCommands().addCommands(unpackCommandData()).queue(); // Creating a guild command using the command data
    }

    /**
     * This method fires everytime the bot is ready. (Everytime it starts up)
     * This method will hold the global commands, and I will be using it to register commands for the server to use.
     *
     * @param event Has all the information about the event.
     */
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        event.getJDA().updateCommands().queue(); // Creating a global command using the command data
    }
}
