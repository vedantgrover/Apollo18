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
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;

public class QueueCommand extends Command {

    public QueueCommand(Apollo18 bot) {
        super(bot);
        this.name = "queue";
        this.description = "Displays the current queue";
        this.category = Category.MUSIC;

        this.args.add(new OptionData(OptionType.INTEGER, "spot", "Skip to a certain position in the queue!", false));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        OptionMapping spot = event.getOption("spot");
        final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("You need to be in a voice channel for this command to work.")).setEphemeral(true).queue();
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        if (spot == null) {

            LinkedList<AudioTrack> queue = musicManager.scheduler.queue;
            AudioTrack audioTrack = musicManager.audioPlayer.getPlayingTrack();

            if (audioTrack == null) {
                event.getHook().sendMessageEmbeds(EmbedUtils.createError("There is nothing playing right now")).queue();
                return;
            }

            StringBuilder nextUpText = new StringBuilder();
            long totalTime = 0;
            for (int i = 0; i < queue.size(); i++) {
                if (i <= 10) {
                    nextUpText.append(i).append(". ").append(queue.get(i).getInfo().title).append("\n");
                }
                totalTime += queue.get(i).getInfo().length;
            }

            Date date = new Date(audioTrack.getInfo().length);
            DateFormat formatter = new SimpleDateFormat("mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String dateFormatted = formatter.format(date);

            Date date2 = new Date(totalTime);
            DateFormat formatter2 = new SimpleDateFormat("HH:mm:ss");
            formatter2.setTimeZone(TimeZone.getTimeZone("UTC"));
            String dateFormatted2 = formatter2.format(date2);

            String url = audioTrack.getInfo().uri;
            String videoID = url.substring(32);
            String thumbnailURL = "http://img.youtube.com/vi/" + videoID + "/0.jpg";

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(audioTrack.getInfo().title);
            embed.setDescription(audioTrack.getInfo().uri);
            embed.addField("Length", dateFormatted, true);
            embed.addField("Artist", audioTrack.getInfo().author, true);
            embed.addField("__Next Up:__", nextUpText.toString(), false);
            embed.setColor(EmbedColor.DEFAULT_COLOR);
            embed.setFooter(((queue.size() > 10) ? "(+" + (queue.size() - 10) + " songs) :: " : "") + "Total Time: " + dateFormatted2);
            embed.setThumbnail(thumbnailURL);

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        } else {
            musicManager.audioPlayer.startTrack(musicManager.scheduler.queue.get(spot.getAsInt()), false);
            for (int i = 0; i < spot.getAsInt(); i++) {
                musicManager.scheduler.queue.remove(i);
            }

            AudioTrack track = musicManager.audioPlayer.getPlayingTrack();

            // The next 4 lines of code just help format the length of the song (returned in milliseconds) into an aesthetically pleasing format.
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

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }
    }
}
