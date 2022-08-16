package com.freyr.apollo18.commands;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.business.BusinessCommand;
import com.freyr.apollo18.commands.business.WorkCommand;
import com.freyr.apollo18.commands.casino.CoinFlipGame;
import com.freyr.apollo18.commands.casino.SlotMachineCommands;
import com.freyr.apollo18.commands.dev.*;
import com.freyr.apollo18.commands.economy.*;
import com.freyr.apollo18.commands.fun.*;
import com.freyr.apollo18.commands.information.*;
import com.freyr.apollo18.commands.leveling.LeaderboardCommand;
import com.freyr.apollo18.commands.leveling.RankCommand;
import com.freyr.apollo18.commands.music.*;
import com.freyr.apollo18.commands.settings.LevelingSettings;
import com.freyr.apollo18.commands.settings.NotificationSettings;
import com.freyr.apollo18.commands.settings.WelcomeSettings;
import com.freyr.apollo18.commands.utility.*;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import com.freyr.apollo18.util.textFormatters.NumberFormatter;
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
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    private static final Map<String, HashMap<String, Long>> cooldowns = new HashMap<>();
    private static final Map<String, HashMap<String, Integer>> numUsers = new HashMap<>();

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
                new MemeCommand(bot), new EmoteCommand(bot), new AvatarCommand(bot), new BinaryCommand(bot), new UrbanDictionaryCommand(bot),

                // Leveling Commands
                new RankCommand(bot), new LeaderboardCommand(bot),

                // Economy Commands
                new BalanceCommand(bot), new DepositCommand(bot), new WithdrawCommand(bot), new BegCommand(bot), new DailyCommand(bot), new RobCommand(bot), new PayCommand(bot),

                // Casino Commands
                new CoinFlipGame(bot), new SlotMachineCommands(bot),

                // Business Commands
                new BusinessCommand(bot), new WorkCommand(bot),

                // Settings Commands
                new WelcomeSettings(bot), new LevelingSettings(bot), new NotificationSettings(bot),

                // Dev only
                new CreateProfile(bot), new CreateUser(bot), new CreateGuild(bot), new CreateDefaultBusiness(bot), new DailyTasks(bot), new CreateDefaultJob(bot),

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
            cooldowns.put(cmd.name, new HashMap<>());
            numUsers.put(cmd.name, new HashMap<>());
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

    private String secondsToDhms(double seconds) {
        double d = Math.floor(seconds / (3600 * 24));
        double h = Math.floor(seconds % (3600 * 24) / 3600);
        double m = Math.floor(seconds % 3600 / 60);
        double s = Math.floor(seconds % 60);

        String dDisplay = d > 0 ? NumberFormatter.formatDoubleToString(d) + (d == 1 ? " day, " : " days ") : "";
        String hDisplay = h > 0 ? NumberFormatter.formatDoubleToString(h) + (h == 1 ? " hour, " : " hours ") : "";
        String mDisplay = m > 0 ? NumberFormatter.formatDoubleToString(m) + (m == 1 ? " minute, " : " minutes ") : "";
        String sDisplay = s > 0 ? NumberFormatter.formatDoubleToString(s) + (s == 1 ? " second" : " seconds") : "";
        return dDisplay + hDisplay + mDisplay + sDisplay;
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

            final long currentTime = System.currentTimeMillis();
            final HashMap<String, Long> timeStamps = cooldowns.get(cmd.name);
            final HashMap<String, Integer> uses = numUsers.get(cmd.name);
            final int cooldownAmount = (cmd.cooldown) * 1000;

            if (timeStamps.containsKey(event.getUser().getId())) {
                final long expirationTime = timeStamps.get(event.getUser().getId()) + cooldownAmount;

                if (currentTime < expirationTime) {
                    final long timeLeft = (expirationTime - currentTime) / 1000;

                    event.replyEmbeds(EmbedUtils.createError("Please wait **" + secondsToDhms(timeLeft) + "** before using the `/" + cmd.name + "` command!")).queue();
                    return;
                }
            }

            if (!uses.containsKey(event.getUser().getId())) uses.put(event.getUser().getId(), 1);

            if (uses.get(event.getUser().getId()) >= cmd.uses) {
                timeStamps.put(event.getUser().getId(), currentTime);

                ScheduledThreadPoolExecutor timeout = new ScheduledThreadPoolExecutor(2);
                timeout.schedule(() -> {
                    timeStamps.remove(event.getUser().getId());
                    uses.remove(event.getUser().getId());
                    uses.put(event.getUser().getId(), 1);
                }, cooldownAmount, TimeUnit.MILLISECONDS);
            }

            int newUse = uses.get(event.getUser().getId()) + 1;
            uses.remove(event.getUser().getId());
            uses.put(event.getUser().getId(), newUse);

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
