package com.freyr.apollo18.data.provider;

import com.freyr.apollo18.data.codec.guild.GuildCodec;
import com.freyr.apollo18.data.records.guild.Guild;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class GuildCodecProvider implements CodecProvider {
    @Override
    @SuppressWarnings("unchecked")
    public <T> Codec<T> get(Class<T> aClass, CodecRegistry codecRegistry) {
        if (aClass == Guild.class) {
            return (Codec<T>) new GuildCodec(codecRegistry);
        }

        return null;
    }
}
