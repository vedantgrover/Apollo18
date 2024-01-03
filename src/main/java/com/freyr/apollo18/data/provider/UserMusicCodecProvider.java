package com.freyr.apollo18.data.provider;

import com.freyr.apollo18.data.codec.user.music.UserMusicCodec;
import com.freyr.apollo18.data.records.user.music.UserMusic;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class UserMusicCodecProvider implements CodecProvider {
    @Override
    @SuppressWarnings("unchecked")
    public <T> Codec<T> get(Class<T> aClass, CodecRegistry codecRegistry) {
        if (aClass == UserMusic.class) {
            return (Codec<T>) new UserMusicCodec(codecRegistry);
        }

        return null;
    }
}
