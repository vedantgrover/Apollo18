package com.freyr.apollo18.data.provider;

import com.freyr.apollo18.data.codec.BusinessCodec;
import com.freyr.apollo18.data.records.business.Business;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class BusinessCodecProvider implements CodecProvider {
    public BusinessCodecProvider() {}

    @Override
    @SuppressWarnings("unchecked")
    public <T> Codec<T> get(Class<T> aClass, CodecRegistry codecRegistry) {
        if (aClass == Business.class) {
            return (Codec<T>) new BusinessCodec(codecRegistry);
        }

        return null;
    }
}
