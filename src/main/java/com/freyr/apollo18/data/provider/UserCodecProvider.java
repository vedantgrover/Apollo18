package com.freyr.apollo18.data.provider;

import com.freyr.apollo18.data.codec.user.UserCodec;
import com.freyr.apollo18.data.records.user.User;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class UserCodecProvider implements CodecProvider {
    @Override
    @SuppressWarnings("unchecked")
    public <T> Codec<T> get(Class<T> aClass, CodecRegistry codecRegistry) {
        if (aClass == User.class) {
            return (Codec<T>) new UserCodec(codecRegistry);
        }

        return null;
    }
}
