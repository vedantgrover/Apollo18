package com.freyr.apollo18.data.codec.user.music;

import com.freyr.apollo18.data.records.user.music.Song;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class SongCodec implements Codec<Song> {
    @Override
    public Song decode(BsonReader bsonReader, DecoderContext decoderContext) {
        bsonReader.readStartDocument();

        String songName = bsonReader.readString("songName");
        String uri = bsonReader.readString("uri");
        int position = bsonReader.readInt32("position");

        bsonReader.readEndDocument();

        return new Song(songName, uri, position);
    }

    @Override
    public void encode(BsonWriter bsonWriter, Song song, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();

        if (song != null) {
            bsonWriter.writeString("songName", song.songName());
            bsonWriter.writeString("uri", song.uri());
            bsonWriter.writeInt32("position", song.position());
        }

        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<Song> getEncoderClass() {
        return Song.class;
    }
}
