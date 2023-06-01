package com.freyr.apollo18.util.music;

import com.freyr.apollo18.util.embeds.EmbedUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioRecorder {
    private final DefaultAudioPlayerManager playerManager;
    private final AudioPlayer audioPlayer;
    private File file;
    private boolean recording;

    public AudioRecorder() {
        this.playerManager = new DefaultAudioPlayerManager();
        this.audioPlayer = playerManager.createPlayer();
        this.file = new File("output.mp3");
        this.recording = false;

        playerManager.getConfiguration().setFilterHotSwapEnabled(true);
    }

    public void init(String voiceChannelID, SlashCommandInteractionEvent event) {
        VoiceChannel voiceChannel = event.getJDA().getVoiceChannelById(voiceChannelID);
        if (voiceChannel == null) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("Voice Channel not found")).queue();
            return;
        }

        AudioManager audioManager = voiceChannel.getGuild().getAudioManager();
        audioManager.openAudioConnection(voiceChannel);

        audioManager.setReceivingHandler(new AudioReceiveHandler() {
            @Override
            public boolean canReceiveCombined() {
                return true;
            }

            @Override
            public boolean canReceiveUser() {
                return false;
            }

            @Override
            public void handleCombinedAudio(CombinedAudio combinedAudio) {
                System.out.println("Handle COmbined Audio");
                if (recording) {
                    byte[] audioData = combinedAudio.getAudioData(1.0);

                    try {
                        FileUtils.writeByteArrayToFile(file, audioData, true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void handleUserAudio(UserAudio userAudio) {

            }
        });
    }

    public void startRecording() {
        System.out.println("Recording Start Triggered");
        recording = true;
    }

    public void stopRecording() {
        System.out.println("Recording Stop Triggered");
        recording = false;
    }
}
