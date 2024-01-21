package com.freyr.apollo18.data.codec.guild;

import com.freyr.apollo18.data.records.guild.Greeting;
import com.freyr.apollo18.data.records.guild.Guild;
import com.freyr.apollo18.data.records.guild.Leveling;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

public class GuildCodec implements Codec<Guild> {

    private final Codec<Leveling> levelingCodec;
    private final Codec<Greeting> greetingCodec;

    public GuildCodec(CodecRegistry codecRegistry) {
        levelingCodec = codecRegistry.get(Leveling.class);
        greetingCodec = codecRegistry.get(Greeting.class);
    }

    @Override
    public Guild decode(BsonReader bsonReader, DecoderContext decoderContext) {
        bsonReader.readStartDocument();

        if (bsonReader.readName().equals("_id")) {
            bsonReader.skipValue();
        }
        String guildId = bsonReader.readString("guildID");

        Leveling leveling = null;
        if (bsonReader.readName().equals("leveling")) {
            leveling = levelingCodec.decode(bsonReader, decoderContext);
        }

        Greeting greeting = null;
        if (bsonReader.readName().equals("greetings")) {
            greeting = greetingCodec.decode(bsonReader, decoderContext);
        }

        bsonReader.readEndDocument();

        return new Guild(guildId, leveling, greeting);
    }

    @Override
    public void encode(BsonWriter bsonWriter, Guild guild, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();

        if (guild != null) {
            bsonWriter.writeString("guildID", guild.guildId());
            bsonWriter.writeName("leveling");
            levelingCodec.encode(bsonWriter, guild.leveling(), encoderContext);
            bsonWriter.writeName("greetings");
            greetingCodec.encode(bsonWriter, guild.greeting(), encoderContext);
        }

        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<Guild> getEncoderClass() {
        return Guild.class;
    }
}
