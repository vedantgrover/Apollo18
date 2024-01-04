package com.freyr.apollo18.data.codec.guild;

import com.freyr.apollo18.data.records.guild.Greeting;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class GreetingCodec implements Codec<Greeting> {
    @Override
    public Greeting decode(BsonReader bsonReader, DecoderContext decoderContext) {
        bsonReader.readStartDocument();

        boolean onOff = bsonReader.readBoolean("onOff");
        String welcomeChannel = readNullableString(bsonReader,"welcomeChannel");
        String leaveChannel = readNullableString(bsonReader, "leaveChannel");
        String memberCountChannel = readNullableString(bsonReader, "memberCountChannel");
        String welcomeMessage = bsonReader.readString("welcomeMessage");
        String leaveMessage = bsonReader.readString("leaveMessage");

        bsonReader.readEndDocument();

        return new Greeting(onOff, welcomeChannel, leaveChannel, memberCountChannel, welcomeMessage, leaveMessage);
    }

    @Override
    public void encode(BsonWriter bsonWriter, Greeting greeting, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();

        if (greeting != null) {
            bsonWriter.writeBoolean("onOff", greeting.onOff());
            writeNullableString(bsonWriter,"welcomeChannel", greeting.welcomeChannel());
            writeNullableString(bsonWriter, "leaveChannel", greeting.leaveChannel());
            writeNullableString(bsonWriter,"memberCountChannel", greeting.memberCountChannel());
            bsonWriter.writeString("welcomeMessage", greeting.welcomeMessage());
            bsonWriter.writeString("leaveMessage", greeting.leaveMessage());
        }

        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<Greeting> getEncoderClass() {
        return Greeting.class;
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
