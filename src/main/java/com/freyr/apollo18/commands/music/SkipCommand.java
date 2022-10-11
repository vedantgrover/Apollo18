package com.freyr.apollo18.commands.music;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import com.freyr.apollo18.util.music.GuildMusicManager;
import com.freyr.apollo18.util.music.PlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * This command skips the current track
 */
public class SkipCommand extends Command {

    public SkipCommand(Apollo18 bot) {
        super(bot);
        this.name = "skip";
        this.description = "Skips the current track";
        this.category = Category.MUSIC;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("You need to be in a voice channel for this command to work.")).setEphemeral(true).queue();
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        AudioPlayer audioPlayer = musicManager.audioPlayer;

        if (audioPlayer.getPlayingTrack() == null) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("There is no track playing currently.")).queue();
            return;
        }

        try {
            musicManager.scheduler.nextTrack();
        } catch (Exception e) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("The queue is now empty!")).queue();
            return;
        }

        event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("Skipped current song")).queue();
    }
}
