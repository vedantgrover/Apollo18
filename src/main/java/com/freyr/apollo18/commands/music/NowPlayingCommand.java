package com.freyr.apollo18.commands.music;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import com.freyr.apollo18.util.music.GuildMusicManager;
import com.freyr.apollo18.util.music.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * This command returns the current track that is being played
 */
public class NowPlayingCommand extends Command {

    public NowPlayingCommand(Apollo18 bot) {
        super(bot);
        this.name = "np";
        this.description = "Shows you what song is currently playing.";
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

        AudioTrack audioTrack = musicManager.audioPlayer.getPlayingTrack();

        Date date = new Date(audioTrack.getInfo().length);
        DateFormat formatter = new SimpleDateFormat("mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateFormatted = formatter.format(date);

        String url = audioTrack.getInfo().uri;
        String videoID = url.substring(32);
        String thumbnailURL = "http://img.youtube.com/vi/" + videoID + "/0.jpg";


        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(audioTrack.getInfo().title);
        embed.setDescription(audioTrack.getInfo().uri);
        embed.addField("Length", dateFormatted, true);
        embed.addField("Artist", audioTrack.getInfo().author, true);
        embed.setColor(EmbedColor.DEFAULT_COLOR);
        embed.setThumbnail(thumbnailURL);

        event.getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
