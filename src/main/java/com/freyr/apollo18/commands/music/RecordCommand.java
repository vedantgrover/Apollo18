package com.freyr.apollo18.commands.music;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import com.freyr.apollo18.util.music.AudioRecorder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;

public class RecordCommand extends Command {

    public RecordCommand(Apollo18 bot) {
        super(bot);

        this.name = "record";
        this.description = "Records activity within a Discord voice channel";
        this.category = Category.MUSIC;

        this.args.add(new OptionData(OptionType.CHANNEL, "channel", "The Voice Channel you want to record", true).setChannelTypes(ChannelType.VOICE));
        this.args.add(new OptionData(OptionType.STRING, "command", "Start or stop the recording", true).addChoice("Start", "Start").addChoice("Stop", "Stop"));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        event.deferReply().queue();

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

        AudioRecorder audioRecorder = new AudioRecorder();
        audioRecorder.init(event.getOption("channel").getAsChannel().getId(), event);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(EmbedColor.DEFAULT_COLOR);
        embed.setDescription("**:white_check_mark: - Joined :loud_sound: `" + memberChannel.getName() + "`!**");

        event.getHook().sendMessageEmbeds(embed.build()).queue();

        switch (event.getOption("command").getAsString().toLowerCase()) {
            case "start":
                System.out.println("Read Start");
                audioRecorder.startRecording();
                event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("Recording Started")).queue();
                break;
            case "stop":
                System.out.println("Read stop");
                audioRecorder.stopRecording();
                // Send the recorded file to Discord using JDA's sendFile() method
                File outputFile = new File("output.mp3");
                event.getHook().sendFiles(FileUpload.fromData(outputFile)).queue();
                outputFile.delete();
                audioManager.closeAudioConnection();
                break;
        }
    }
}
