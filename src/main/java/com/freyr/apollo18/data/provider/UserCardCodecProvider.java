package com.freyr.apollo18.data.provider;

import com.freyr.apollo18.data.codec.user.economy.UserCardCodec;
import com.freyr.apollo18.data.records.user.economy.UserCard;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class UserCardCodecProvider implements CodecProvider {
    @Override
    @SuppressWarnings("unchecked")
    public <T> Codec<T> get(Class<T> aClass, CodecRegistry codecRegistry) {
        if (aClass == UserCard.class) {
            return (Codec<T>) new UserCardCodec(codecRegistry);
        }

        return null;
    }
}
