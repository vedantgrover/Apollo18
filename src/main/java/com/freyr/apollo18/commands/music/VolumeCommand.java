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
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class VolumeCommand extends Command {

    public VolumeCommand() {
        super();
        this.name = "volume";
        this.description = "Sets the volume of the music";
        this.category = Category.MUSIC;

        OptionData data = new OptionData(OptionType.INTEGER, "volume", "New Volume Percent", false);
        data.setMaxValue(200);
        data.setMinValue(0);
        this.args.add(data);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        OptionMapping volume = event.getOption("volume");

        final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("You need to be in a voice channel for this command to work.")).setEphemeral(true).queue();
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        if (volume == null) {
            event.getHook().sendMessageEmbeds(new EmbedBuilder().setColor(EmbedColor.DEFAULT_COLOR).setDescription("**:loud_sound: - Current volume is set to " + musicManager.audioPlayer.getVolume() + "%**").build()).queue();
        } else {
            musicManager.audioPlayer.setVolume(volume.getAsInt());

            event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("Volume has been set to " + musicManager.audioPlayer.getVolume() + "%")).queue();
        }
    }
}
