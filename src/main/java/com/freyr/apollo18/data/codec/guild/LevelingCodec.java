package com.freyr.apollo18.data.codec.guild;

import com.freyr.apollo18.data.records.guild.Leveling;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class LevelingCodec implements Codec<Leveling> {
    @Override
    public Leveling decode(BsonReader bsonReader, DecoderContext decoderContext) {
        bsonReader.readStartDocument();

        boolean onOff = bsonReader.readBoolean("onOff");
        String channel = readNullableString(bsonReader, "channel");
        String levelingMessage = bsonReader.readString("levelingMessage");

        bsonReader.readEndDocument();

        return new Leveling(onOff, channel, levelingMessage);
    }

    @Override
    public void encode(BsonWriter bsonWriter, Leveling leveling, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();

        if (leveling != null) {
            bsonWriter.writeBoolean("onOff", leveling.onOff());
            writeNullableString(bsonWriter, "channel", leveling.channel());
            bsonWriter.writeString("levelingMessage", leveling.levelingMessage());
        }

        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<Leveling> getEncoderClass() {
        return Leveling.class;
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
