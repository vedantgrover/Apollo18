package com.freyr.apollo18.util.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

/**
 * This is a holder for both the player and a track scheduler for one guild
 *
 * @author Freyr
 */
public class GuildMusicManager {
    public final AudioPlayer audioPlayer; // This is our audio player. It will control the music and let us control different settings like volume, pause-resume etc.
    public final TrackScheduler scheduler; // This controls all events. It allows the bot to know when the music is paused or resumed.

    /**
     * Creates a player and a track scheduler.
     *
     * @param manager Audio player manager to use for creating the player.
     */
    public GuildMusicManager(AudioPlayerManager manager) {
        this.audioPlayer = manager.createPlayer();
        this.scheduler = new TrackScheduler(this.audioPlayer);
        this.audioPlayer.addListener(this.scheduler);
    }

    /**
     * Gets the {@link AudioPlayerSendHandler} for the guild.
     *
     * @return Wrapper around AudioPlayer to use it as an AudioSendHandler
     */
    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(audioPlayer);
    }
}
