package com.freyr.apollo18.util.music;

import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class searches and plays the song.
 *
 * @author Freyr
 */
public class PlayerManager {
    public static MessageChannel musicLogChannel; // This is the channel that the user put the command in.
    private static PlayerManager INSTANCE; // This is the class
    private final Map<Long, GuildMusicManager> musicManagers; // We are mapping each guild manager to that guild id (taken from Discord)
    private final AudioPlayerManager audioPlayerManager; // Allows us to load the track and play it

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        // Registering Sources
        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    /**
     * If the player manager doesn't exist yet, then it creates a new instance of it.
     *
     * @return A new player manager
     */
    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }

    /**
     * Gets the GuildMusicManager. This allows multiple guilds to play music at the same time.
     *
     * @param guild The guild you want the music manager for
     * @return The Music Manager for that Guild
     */
    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);

            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

            return guildMusicManager;
        });
    }

    /**
     * Grabs the song/playlist and adds it to the queue or plays it.
     *
     * @param event    I needed this event so that I can send a message
     * @param channel  This is the channel that the user used the command in.
     * @param trackUrl This is the Url or Search that the user put in
     */
    public void loadAndPlay(SlashCommandInteractionEvent event, MessageChannel channel, String trackUrl) {
        final GuildMusicManager musicManager = this.getMusicManager(event.getGuild());
        musicLogChannel = channel;

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                musicManager.scheduler.queue(audioTrack);

                Date date = new Date(audioTrack.getInfo().length);
                DateFormat formatter = new SimpleDateFormat("mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                String dateFormatted = formatter.format(date);

                String url = audioTrack.getInfo().uri;
                String videoID = url.substring(32);
                String thumbnailURL = "http://img.youtube.com/vi/" + videoID + "/0.jpg";


                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle(audioTrack.getInfo().title);
                embed.setDescription("Song added to queue. Number: " + musicManager.scheduler.queue.size());
                embed.addField("Length", dateFormatted, true);
                embed.addField("Artist", audioTrack.getInfo().author, true);
                embed.setColor(EmbedColor.DEFAULT_COLOR);
                embed.setThumbnail(thumbnailURL);

                event.getHook().sendMessageEmbeds(embed.build()).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                final List<AudioTrack> tracks = audioPlaylist.getTracks();

                if (audioPlaylist.isSearchResult()) {
                    trackLoaded(tracks.get(0));
                } else {
                    for (AudioTrack track : tracks) {
                        musicManager.scheduler.queue(track);
                    }

                    EmbedBuilder embed = new EmbedBuilder();

                    embed.setTitle(audioPlaylist.getName());
                    embed.setDescription("Added " + tracks.size() + " songs in the queue.");
                    embed.setColor(EmbedColor.DEFAULT_COLOR);

                    event.getHook().sendMessageEmbeds(embed.build()).queue();
                }
            }

            @Override
            public void noMatches() {
                event.getHook().sendMessageEmbeds(EmbedUtils.createError("We were unable to find your search.")).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });
    }
}
