package com.freyr.apollo18.data.codec.guild;

import com.freyr.apollo18.data.records.guild.Greeting;
import com.freyr.apollo18.util.DataUtility;
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
        String welcomeChannel = DataUtility.readNullableString(bsonReader, "welcomeChannel");
        String leaveChannel = DataUtility.readNullableString(bsonReader, "leaveChannel");
        String memberCountChannel = DataUtility.readNullableString(bsonReader, "memberCountChannel");
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
            DataUtility.writeNullableString(bsonWriter, "welcomeChannel", greeting.welcomeChannel());
            DataUtility.writeNullableString(bsonWriter, "leaveChannel", greeting.leaveChannel());
            DataUtility.writeNullableString(bsonWriter, "memberCountChannel", greeting.memberCountChannel());
            bsonWriter.writeString("welcomeMessage", greeting.welcomeMessage());
            bsonWriter.writeString("leaveMessage", greeting.leaveMessage());
        }

        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<Greeting> getEncoderClass() {
        return Greeting.class;
    }
}
