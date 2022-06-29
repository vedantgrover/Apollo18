package com.freyr.apollo18.util.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.ByteBuffer;

/**
 * This is a wrapper around AudioPlayer which makes it behave as an AudioSendHandler for JDA. As JDA calls canProvide
 * before every call to provide20MsAudio(), we pull the frame in canProvide() and use the frame we already pulled in
 * provide20MsAudio().
 *
 * @author Freyr
 */
public class AudioPlayerSendHandler implements AudioSendHandler {
    private final AudioPlayer audioPlayer; // This is our audio player. It will control the music and let us control different settings like volume, pause-resume etc.
    private final ByteBuffer buffer; // This allows us to transfer a certain amount of bytes from source to a destination.
    private final MutableAudioFrame frame; // Consists of the set of samples for all channels at a given point in time

    /**
     * Constructor for the wrapper class
     *
     * @param audioPlayer Audio player to wrap
     */
    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.buffer = ByteBuffer.allocate(1024);
        this.frame = new MutableAudioFrame();
        this.frame.setBuffer(buffer);
    }

    /**
     * Only returns true if the audio was provided from the frame.
     *
     * @return True if the audio was provided from the frame.
     */
    @Override
    public boolean canProvide() {
        return this.audioPlayer.provide(this.frame);
    }

    /**
     * Flipping the buffer to make it a read buffer.
     *
     * @return The read buffer after buffer has been flipped
     */
    @Override
    public ByteBuffer provide20MsAudio() {
        return this.buffer.flip();
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
