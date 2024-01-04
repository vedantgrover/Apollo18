package com.freyr.apollo18.data.codec.user.music;

import com.freyr.apollo18.data.records.user.music.Playlist;
import com.freyr.apollo18.data.records.user.music.UserMusic;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.ArrayList;
import java.util.List;

public class UserMusicCodec implements Codec<UserMusic> {

    private final Codec<Playlist> playlistCodec;

    public UserMusicCodec(CodecRegistry codecRegistry) {
        playlistCodec = codecRegistry.get(Playlist.class);
    }

    @Override
    public UserMusic decode(BsonReader bsonReader, DecoderContext decoderContext) {
        bsonReader.readStartDocument();
        List<Playlist> playlists = readPlaylists(bsonReader, decoderContext);

        bsonReader.readEndDocument();

        return new UserMusic(playlists);
    }

    @Override
    public void encode(BsonWriter bsonWriter, UserMusic music, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();

        if (music != null) {
            writePlaylists(bsonWriter, music.playlists(), encoderContext);
        }

        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<UserMusic> getEncoderClass() {
        return UserMusic.class;
    }

    private List<Playlist> readPlaylists(BsonReader bsonReader, DecoderContext decoderContext) {
        List<Playlist> playlists = new ArrayList<>();
        if (bsonReader.readBsonType() == BsonType.ARRAY) {
            bsonReader.readStartArray();
            while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                playlists.add(playlistCodec.decode(bsonReader, decoderContext));
            }
            bsonReader.readEndArray();
        }
        return playlists;
    }

    private void writePlaylists(BsonWriter bsonWriter, List<Playlist> playlists, EncoderContext encoderContext) {
        bsonWriter.writeStartArray("playlists");
        for (Playlist playlist : playlists) {
            playlistCodec.encode(bsonWriter, playlist, encoderContext);
        }
        bsonWriter.writeEndArray();
    }
}
