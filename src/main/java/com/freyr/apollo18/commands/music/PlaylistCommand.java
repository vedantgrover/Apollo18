package com.freyr.apollo18.commands.music;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.data.Database;
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
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.bson.Document;

import java.util.List;

public class PlaylistCommand extends Command {

    public PlaylistCommand(Apollo18 bot) {
        super(bot);
        this.name = "playlist";
        this.description = "Interact with your saved playlists!";
        this.category = Category.MUSIC;
        this.devOnly = false;

        this.subCommands.add(new SubcommandData("see", "See all of your playlists!").addOption(OptionType.STRING, "playlist", "List a playlist you want to see."));
        this.subCommands.add(new SubcommandData("create", "Create a new playlist to store all of your great songs!").addOption(OptionType.STRING, "name", "Your new playlist name", true));
        this.subCommands.add(new SubcommandData("add", "Add the currently playing tune to your playlist").addOption(OptionType.STRING, "playlist", "The playlist you want to add this song to.", true));
        this.subCommands.add(new SubcommandData("remove-song", "Remove a song from your playlist").addOption(OptionType.STRING, "playlist", "The playlist you want to remove the song from", true).addOption(OptionType.STRING, "song", "The song you want to remove", true));
        this.subCommands.add(new SubcommandData("remove", "Remove a playlist").addOption(OptionType.STRING, "playlist", "The playlist you want to remove", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        Database db = bot.getDatabase();

        final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        switch (event.getSubcommandName()) {
            case "see" -> {
                OptionMapping playlist = event.getOption("playlist");
                if (playlist == null) {
                    StringBuilder playlists = new StringBuilder();
                    List<Document> userPlaylists = db.getPlaylists(event.getUser().getId());
                    for (int i = 0; i < userPlaylists.size(); i++) {
                        playlists.append("**").append(i + 1).append(")** `").append(userPlaylists.get(i).getString("playlistName")).append("` - **").append(userPlaylists.get(i).getList("songs", Document.class).size()).append(" songs**\n");
                    }

                    EmbedBuilder embed = new EmbedBuilder();

                    embed.setTitle(event.getUser().getName() + "'s Playlists");
                    embed.addField("Playlists", playlists.toString(), false);
                    embed.setFooter(userPlaylists.size() + " playlists", event.getUser().getAvatarUrl());
                    embed.setColor(EmbedColor.DEFAULT_COLOR);

                    event.getHook().sendMessageEmbeds(embed.build()).queue();
                } else {
                    String userPlaylist = playlist.getAsString();

                    StringBuilder songs = new StringBuilder();
                    List<Document> userSongs = db.getSongs(event.getUser().getId(), userPlaylist);
                    for (int i = 0; i < userSongs.size(); i++) {
                        songs.append("**").append(i + 1).append(")** `").append(userSongs.get(i).getString("songName")).append("`\n");
                        if (i > 10) {
                            break;
                        }
                    }


                    EmbedBuilder embed = new EmbedBuilder();

                    embed.setTitle(event.getUser().getName() + "'s Songs");
                    embed.setThumbnail(event.getUser().getAvatarUrl());
                    embed.addField("Songs", songs.toString(), false);
                    embed.setFooter(userSongs.size() + " songs", event.getUser().getAvatarUrl());
                    embed.setColor(EmbedColor.DEFAULT_COLOR);

                    event.getHook().sendMessageEmbeds(embed.build()).queue();
                }
            }

            case "create" -> {
                if (db.getPlaylists(event.getUser().getId()).size() > 10) {
                    event.getHook().sendMessageEmbeds(EmbedUtils.createError("You have reached the max number of playlists")).queue();
                    return;
                }
                db.createPlaylist(event.getUser().getId(), event.getOption("name").getAsString());
                event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess(event.getOption("name").getAsString() + " has been successfully created.")).queue();
            }

            case "add" -> {
                if (!memberVoiceState.inAudioChannel()) {
                    event.getHook().sendMessageEmbeds(EmbedUtils.createError("You need to be in a voice channel for this command to work.")).setEphemeral(true).queue();
                    return;
                }

                if (musicManager.audioPlayer.getPlayingTrack() == null) {
                    event.getHook().sendMessageEmbeds(EmbedUtils.createError("Nothing is playing right now.")).queue();
                    return;
                }

                if (db.getPlaylists(event.getUser().getId()).size() == 0) {
                    event.getHook().sendMessageEmbeds(EmbedUtils.createError("You currently do not have any playlists!")).queue();
                    return;
                }

                try {
                    db.addSong(event.getUser().getId(), event.getOption("playlist").getAsString(), musicManager.audioPlayer.getPlayingTrack());
                } catch (Exception e) {
                    event.getHook().sendMessageEmbeds(EmbedUtils.createError("We could not find that playlist")).queue();
                    return;
                }
                event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess(musicManager.audioPlayer.getPlayingTrack().getInfo().title + " has been added to " + event.getOption("playlist").getAsString())).queue();
            }

            case "remove-song" -> {
                db.removeSong(event.getUser().getId(), event.getOption("playlist").getAsString(), event.getOption("song").getAsString());
                event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess(event.getOption("song").getAsString() + " has been removed from the playlist.")).queue();
            }

            case "remove" -> {
                event.getHook().sendMessageEmbeds(EmbedUtils.createNotification("Coming soon. Dunno why this hasn't been made yet. I am doing it. Yes. Thank you")).queue();
            }
        }

    }
}
