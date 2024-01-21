package com.freyr.apollo18.data.records.user.music;

import java.util.List;

public record Playlist(String playlistName, List<Song> songs) {
}
