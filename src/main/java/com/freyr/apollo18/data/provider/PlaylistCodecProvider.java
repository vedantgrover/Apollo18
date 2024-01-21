package com.freyr.apollo18.data.provider;

import com.freyr.apollo18.data.codec.user.music.PlaylistCodec;
import com.freyr.apollo18.data.records.user.music.Playlist;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class PlaylistCodecProvider implements CodecProvider {
    @Override
    @SuppressWarnings("unchecked")
    public <T> Codec<T> get(Class<T> aClass, CodecRegistry codecRegistry) {
        if (aClass == Playlist.class) {
            return (Codec<T>) new PlaylistCodec(codecRegistry);
        }

        return null;
    }
}
