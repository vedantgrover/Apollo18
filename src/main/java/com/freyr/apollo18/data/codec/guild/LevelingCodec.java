package com.freyr.apollo18.data.codec.guild;

import com.freyr.apollo18.data.records.guild.Leveling;
import com.freyr.apollo18.util.DataUtility;
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
        String channel = DataUtility.readNullableString(bsonReader, "channel");
        String levelingMessage = bsonReader.readString("levelingMessage");

        bsonReader.readEndDocument();

        return new Leveling(onOff, channel, levelingMessage);
    }

    @Override
    public void encode(BsonWriter bsonWriter, Leveling leveling, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();

        if (leveling != null) {
            bsonWriter.writeBoolean("onOff", leveling.onOff());
            DataUtility.writeNullableString(bsonWriter, "channel", leveling.channel());
            bsonWriter.writeString("levelingMessage", leveling.levelingMessage());
        }

        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<Leveling> getEncoderClass() {
        return Leveling.class;
    }
}
