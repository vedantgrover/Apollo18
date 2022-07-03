package com.freyr.apollo18.commands.music;

import com.freyr.apollo18.Apollo18;
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

/**
 * This command is part of the music system. It allows us to loop songs
 */
public class LoopCommand extends Command {

    public LoopCommand(Apollo18 bot) {
        super(bot);
        this.name = "loop";
        this.description = "Loops the current song";
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
        musicManager.scheduler.isLoop = !musicManager.scheduler.isLoop;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("**:white_check_mark: - Loop has been " + ((musicManager.scheduler.isLoop) ? "enabled" : "disabled") + "**");
        embed.setColor(EmbedColor.DEFAULT_COLOR);

        event.getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
