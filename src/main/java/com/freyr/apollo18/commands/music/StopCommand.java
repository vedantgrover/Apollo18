package com.freyr.apollo18.commands.music;

import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import com.freyr.apollo18.util.music.GuildMusicManager;
import com.freyr.apollo18.util.music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class StopCommand extends Command {

    public StopCommand() {
        super();
        this.name = "stop";
        this.description = "Stops your music, clears your queue, and leaves the voice channel";
        this.category = Category.MUSIC;
    }

    private static void leave(SlashCommandInteractionEvent event) {
        event.getGuild().getAudioManager().closeAudioConnection();
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

        musicManager.scheduler.player.stopTrack();
        musicManager.scheduler.queue.clear();
        leave(event);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(EmbedColor.DEFAULT_COLOR);
        embed.setDescription("**:white_check_mark: - Stopped the music and cleared the queue.**");

        event.getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
