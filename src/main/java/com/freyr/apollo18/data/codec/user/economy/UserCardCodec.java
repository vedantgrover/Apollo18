package com.freyr.apollo18.data.codec.user.economy;

import com.freyr.apollo18.data.records.user.economy.UserCard;
import com.freyr.apollo18.data.records.user.economy.UserCreditCard;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

public class UserCardCodec implements Codec<UserCard> {
    private final Codec<UserCreditCard> userCreditCardCodec;

    public UserCardCodec(CodecRegistry codecRegistry) {
        userCreditCardCodec = codecRegistry.get(UserCreditCard.class);
    }

    @Override
    public UserCard decode(BsonReader bsonReader, DecoderContext decoderContext) {
        bsonReader.readStartDocument();

        boolean debitCard = bsonReader.readBoolean("debitCard");
        UserCreditCard userCreditCard = null;
        if (bsonReader.readName().equals("creditCard")) {
            userCreditCard = userCreditCardCodec.decode(bsonReader, decoderContext);
        }

        bsonReader.readEndDocument();

        return new UserCard(debitCard, userCreditCard);
    }

    @Override
    public void encode(BsonWriter bsonWriter, UserCard userCard, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();

        if (userCard != null) {
            bsonWriter.writeBoolean("debitCard", userCard.debitCard());
            bsonWriter.writeName("creditCard");
            userCreditCardCodec.encode(bsonWriter, userCard.creditCard(), encoderContext);
        }

        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<UserCard> getEncoderClass() {
        return UserCard.class;
    }
}
