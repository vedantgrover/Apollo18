package com.freyr.apollo18.util.music;

import com.freyr.apollo18.util.embeds.EmbedColor;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.EmbedBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 *
 * @author Freyr
 */
public class TrackScheduler extends AudioEventAdapter {

    public final AudioPlayer player; // This is our audio player. It will control the music and let us control different settings like volume, pause-resume etc.
    public final LinkedList<AudioTrack> queue; // This is a list of songs that the user has requested
    public boolean isLoop = false; // Checks to see if looping is on.

    /**
     * Creates an instance of the TrackScheduler
     *
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedList<>();
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue
     *
     * @param track The track to play or add to queue
     */
    public void queue(AudioTrack track) {
        /*
         * startTrack's boolean parameter controls when to start the song. If it is set to true, then it will only play
         * the song when nothing else is playing. If something else is currently playing, then the track is added to the
         * queue. Otherwise, it plays the track.
         * */
        if (!this.player.startTrack(track, true)) {
            this.queue.add(track);
        }
    }

    /**
     * Starts the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        AudioTrack track = this.queue.remove(0); // Grabbing the next song in the queue
        this.player.startTrack(track, false); // Stopping the current song and playing the new song.

        // The next 4 lines of code just help formatLongNumber the length of the song (returned in milliseconds) into an aesthetically pleasing formatLongNumber.
        Date date = new Date(track.getInfo().length);
        DateFormat formatter = new SimpleDateFormat("mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateFormatted = formatter.format(date);

        // The next 3 lines of code grabs the URL of the thumbnail
        String url = track.getInfo().uri;
        String videoID = url.substring(32);
        String thumbnailURL = "http://img.youtube.com/vi/" + videoID + "/0.jpg";

        // Building the embed that the user will see
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(track.getInfo().title);
        embed.setDescription(track.getInfo().uri);
        embed.addField("Length", dateFormatted, true);
        embed.addField("Artist", track.getInfo().author, true);
        embed.setColor(EmbedColor.DEFAULT_COLOR);
        embed.setThumbnail(thumbnailURL);

        PlayerManager.musicLogChannel.sendMessageEmbeds(embed.build()).queue(); // Sending the embed in the channel the user played the command in.
    }

    /**
     * This method fires everytime the track is paused.
     *
     * @param player The audio player
     */
    @Override
    public void onPlayerPause(AudioPlayer player) {
        super.onPlayerPause(player);
    }

    /**
     * This method fires everytime the track is resumed.
     *
     * @param player The audio player
     */
    @Override
    public void onPlayerResume(AudioPlayer player) {
        super.onPlayerResume(player);
    }

    /**
     * This method fires everytime a track is started
     *
     * @param player The audio player
     * @param track  The track that started playing
     */
    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        super.onTrackStart(player, track);
    }

    /**
     * This method fires everytime the track ends
     *
     * @param player    The audio player
     * @param track     The track that ended
     * @param endReason This allows us to see if we can start the next track
     */
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) { // If we can start the next song
            if (this.isLoop) { // If the song is set to loop
                this.player.startTrack(track.makeClone(), false); // Make a clone of the current song and play it
                return;
            }

            nextTrack(); // Go to the next track in the queue
        }
    }
}
