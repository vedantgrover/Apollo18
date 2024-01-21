package com.freyr.apollo18.commands.music;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
import com.freyr.apollo18.data.records.user.music.Song;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import com.freyr.apollo18.util.music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * This command connects to a voice channel and plays music
 */
public class PlayCommand extends Command {


    public PlayCommand(Apollo18 bot) {
        super(bot);
        this.name = "play";
        this.description = "Plays a song either from a search or from a link.";
        this.category = Category.MUSIC;
        this.botPermission = Permission.VOICE_CONNECT;

        this.args.add(new OptionData(OptionType.STRING, "song", "Enter in a song search or song/playlist link"));
        this.args.add(new OptionData(OptionType.STRING, "playlist", "Enter in the name of your playlist."));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        final Member self = event.getGuild().getSelfMember();

        if (!self.getVoiceState().inAudioChannel()) {
            join(event);
        }

        final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("You need to be in a voice channel for this command to work.")).setEphemeral(true).queue();
            return;
        }

        OptionMapping songOption = event.getOption("song");
        OptionMapping playlistOption = event.getOption("playlist");

        if ((songOption == null && playlistOption == null) || (songOption != null && playlistOption != null)) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("Please pick 1 of the options.")).queue();
        } else if (songOption != null) {
            String song = event.getOption("song").getAsString();
            String link = String.join(" ", song);

            if (!isUrl(link)) {
                link = "ytsearch:" + link;
            }

            PlayerManager.getInstance().loadAndPlay(event, event.getChannel(), link);
        } else if (playlistOption != null) {
            Database db = bot.getDatabase();
            List<Song> songs = db.getSongs(event.getUser().getId(), event.getOption("playlist").getAsString().toLowerCase());

            for (Song songData : songs) {
                PlayerManager.getInstance().loadAndPlay(event, event.getChannel(), songData.uri());
            }
        }
    }

    private void join(SlashCommandInteractionEvent event) {
        final Member self = event.getGuild().getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        if (selfVoiceState.inAudioChannel() && !selfVoiceState.getChannel().equals(event.getMember().getVoiceState().getChannel())) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("The bot is already in a voice channel: " + selfVoiceState.getChannel().getAsMention())).queue();
            return;
        }

        final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("Please join a voice channel first")).queue();
            return;
        }

        final AudioManager audioManager = event.getGuild().getAudioManager();
        final AudioChannel memberChannel = memberVoiceState.getChannel();

        if (!self.hasPermission(Permission.VOICE_CONNECT) && !self.hasPermission(Permission.ADMINISTRATOR)) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("I am missing the `CONNECT` permission")).queue();
            return;
        }

        audioManager.openAudioConnection(memberChannel);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(EmbedColor.DEFAULT_COLOR);
        embed.setDescription("**:white_check_mark: - Joined :loud_sound: `" + memberChannel.getName() + "`!**");

        event.getHook().sendMessageEmbeds(embed.build()).queue();
    }

    private boolean isUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
