package com.freyr.apollo18.data.codec.user.economy;

import com.freyr.apollo18.data.records.user.economy.UserCreditCard;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class UserCreditCardCodec implements Codec<UserCreditCard> {
    @Override
    public UserCreditCard decode(BsonReader bsonReader, DecoderContext decoderContext) {
        bsonReader.readStartDocument();

        boolean hasCard = bsonReader.readBoolean("hasCard");
        int currentBalance = bsonReader.readInt32("currentBalance");
        int totalBalance = bsonReader.readInt32("totalBalance");
        String expirationDate = readNullableString(bsonReader, "expirationDate");

        bsonReader.readEndDocument();

        return new UserCreditCard(hasCard, currentBalance, totalBalance, expirationDate);
    }

    @Override
    public void encode(BsonWriter bsonWriter, UserCreditCard userCreditCard, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();

        if (userCreditCard != null) {
            bsonWriter.writeBoolean("currentBalance", userCreditCard.hasCard());
            bsonWriter.writeInt32("currentBalance", userCreditCard.currentBalance());
            bsonWriter.writeInt32("totalBalance", userCreditCard.totalBalance());
            writeNullableString(bsonWriter, "expirationDate", userCreditCard.expirationDate());
        }

        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<UserCreditCard> getEncoderClass() {
        return UserCreditCard.class;
    }

    private String readNullableString(BsonReader bsonReader, String fieldName) {
        if (bsonReader.readBsonType() == BsonType.NULL) {
            bsonReader.readNull();
            return null;
        }

        return bsonReader.readString(fieldName);
    }

    private void writeNullableString(BsonWriter writer, String fieldName, String value) {
        if (value == null) {
            writer.writeNull(fieldName);
        } else {
            writer.writeString(fieldName, value);
        }
    }
}
