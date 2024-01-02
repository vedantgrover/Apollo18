package com.freyr.apollo18.data.provider;

import com.freyr.apollo18.data.codec.user.economy.UserEconomyCodec;
import com.freyr.apollo18.data.records.user.economy.UserEconomy;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class UserEconomyCodecProvider implements CodecProvider {
    @Override
    @SuppressWarnings("unchecked")
    public <T> Codec<T> get(Class<T> aClass, CodecRegistry codecRegistry) {
        if (aClass == UserEconomy.class) {
            return (Codec<T>) new UserEconomyCodec(codecRegistry);
        }

        return null;
    }
}
