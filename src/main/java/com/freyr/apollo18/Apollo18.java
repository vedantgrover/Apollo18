package com.freyr.apollo18;

import com.freyr.apollo18.commands.CommandManager;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.listeners.BotListener;
import com.freyr.apollo18.listeners.ButtonListener;
import com.freyr.apollo18.listeners.GuildListener;
import com.freyr.apollo18.listeners.LevelingListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main class where we initialize the bot
 *
 * @author Freyr
 */
public class Apollo18 {

    private final @NotNull Dotenv config; // Getting all of my sensitive info from environment file
    private final @NotNull ShardManager shardManager; // Allows bot to run on multiple servers. Bot "builder"
    private final @NotNull Database database;

    public Apollo18() throws LoginException {
        config = Dotenv.configure().ignoreIfMissing().load(); // Initializing and loading the .env file if it exists in the classpath.
        database = new Database(config.get("DATABASE", System.getenv("DATABASE")), this);

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(config.get("TOKEN", System.getenv("TOKEN"))); // Creating a basic instance of the bot and logging in with token
        builder.setStatus(OnlineStatus.ONLINE); // Setting the bot status to ONLINE (Green Dot)
        builder.setActivity(Activity.playing("/help")); // Setting the bot activity to "Freyr fail...." (I will change this)
        builder.enableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES); // Enabling Gateway Intents for the bot to have more access to user information (ROLES, MESSAGES)

        // Caches are important to retrieve user data. This would be a requirement in some events
        builder.setMemberCachePolicy(MemberCachePolicy.ALL); // This sets the number of users to Cache in a guild. Lazy loading. Cache's them using Discord.
        builder.setChunkingFilter(ChunkingFilter.ALL); // Forces bot to cache all users on start up (If set to ChunkingFilter.ALL [Make sure to make the memberCachePolicy "all" for the ChunkingFiler.all]).
        builder.enableCache(CacheFlag.VOICE_STATE); // ONLY TURN THIS ON IF CACHE IS ON. Picks what to cache about the user (ACTIVITY, CLIENT_STATUS, EMOTE, MEMBER_OVERRIDES, ONLINE_STATUS, ROLE_TAGS, VOICE_STATES)

        shardManager = builder.build(); // Creating the bot

        // Registering Listeners
        shardManager.addEventListener(new GuildListener(this), new ButtonListener(), new LevelingListener(this), new CommandManager(this), new BotListener(this));

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Los_Angeles"));
        ZonedDateTime nextRun = now.withHour(1).withMinute(0).withSecond(0);
        if(now.compareTo(nextRun) > 0) {
            nextRun = nextRun.plusDays(1);
        }

        Duration duration = Duration.between(now, nextRun);
        long initialDelay = duration.getSeconds();
        System.out.println("Will run in: " + initialDelay);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Running");
            database.updateStocks();
            database.dailyWorkChecks();
        }, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);

        System.out.println("Will run at " + nextRun.format(DateTimeFormatter.ofPattern("yyyy/MM/dd-hh:mm:ss")));
    }

    public static void main(String[] args) {
        try {
            new Apollo18(); // Starting the bot
        } catch (LoginException e) {
            System.out.println("ERROR: Provided bot token is invalid"); // Exception handling if the bot token is invalid
        }
    }

    /**
     * Gets the shard manager.
     * The shard manager builds the bot and helps set its properties.
     *
     * @return Shard Manager
     */
    public @NotNull ShardManager getShardManager() {
        return shardManager;
    }

    /**
     * Gets the config variables
     * The config variables are located in the .env file.
     * These variables are secret variables and should not be shared
     *
     * @return Config
     */
    public @NotNull Dotenv getConfig() {
        return config;
    }

    public @NotNull Database getDatabase() {
        return database;
    }
}
