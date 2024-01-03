package com.freyr.apollo18.data.codec.user.music;

import com.freyr.apollo18.data.records.user.music.Playlist;
import com.freyr.apollo18.data.records.user.music.Song;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.ArrayList;
import java.util.List;

public class PlaylistCodec implements Codec<Playlist> {
    private final Codec<Song> songCodec;

    public PlaylistCodec(CodecRegistry codecRegistry) {
        songCodec = codecRegistry.get(Song.class);
    }

    @Override
    public Playlist decode(BsonReader bsonReader, DecoderContext decoderContext) {
        bsonReader.readStartDocument();

        String playlistName = bsonReader.readString("playlistName");
        List<Song> songs = readSongs(bsonReader, decoderContext);

        return new Playlist(playlistName, songs);
    }

    @Override
    public void encode(BsonWriter bsonWriter, Playlist playlist, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();

        if (playlist != null) {
            bsonWriter.writeString("playlistName", playlist.playlistName());
            writeSongs(bsonWriter, playlist.songs(), encoderContext);
        }

        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<Playlist> getEncoderClass() {
        return Playlist.class;
    }

    private List<Song> readSongs(BsonReader bsonReader, DecoderContext decoderContext) {
        List<Song> songs = new ArrayList<>();
        if (bsonReader.readBsonType() == BsonType.ARRAY) {
            bsonReader.readStartArray();
            while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                songs.add(songCodec.decode(bsonReader, decoderContext));
            }
            bsonReader.readEndArray();
        }
        return songs;
    }

    private void writeSongs(BsonWriter bsonWriter, List<Song> songs, EncoderContext encoderContext) {
        bsonWriter.writeStartArray("songs");
        for (Song song : songs) {
            songCodec.encode(bsonWriter, song, encoderContext);
        }
        bsonWriter.writeEndArray();
    }
}
