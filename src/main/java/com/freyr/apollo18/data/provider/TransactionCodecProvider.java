package com.freyr.apollo18.data.provider;

import com.freyr.apollo18.data.codec.TransactionCodec;
import com.freyr.apollo18.data.records.Transaction;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class TransactionCodecProvider implements CodecProvider {
    @Override
    @SuppressWarnings("unchecked")
    public <T> Codec<T> get(Class<T> aClass, CodecRegistry codecRegistry) {
        if (aClass == Transaction.class) {
            return (Codec<T>) new TransactionCodec();
        }

        return null;
    }
}
