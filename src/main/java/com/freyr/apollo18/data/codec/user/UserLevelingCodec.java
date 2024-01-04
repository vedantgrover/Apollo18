package com.freyr.apollo18.data.codec.user;

import com.freyr.apollo18.data.records.user.UserLeveling;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class UserLevelingCodec implements Codec<UserLeveling> {
    @Override
    public UserLeveling decode(BsonReader bsonReader, DecoderContext decoderContext) {
        bsonReader.readStartDocument();

        String guildID = bsonReader.readString("guildID");
        int xp = bsonReader.readInt32("xp");
        int level = bsonReader.readInt32("level");
        int totalXp = bsonReader.readInt32("totalXp");
        boolean inServer = bsonReader.readBoolean("inServer");

        bsonReader.readEndDocument();

        return new UserLeveling(guildID, xp, level, totalXp, inServer);
    }

    @Override
    public void encode(BsonWriter bsonWriter, UserLeveling userLeveling, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();

        if (userLeveling != null) {
            bsonWriter.writeString("guildID", userLeveling.guildID());
            bsonWriter.writeInt32("xp", userLeveling.xp());
            bsonWriter.writeInt32("level", userLeveling.level());
            bsonWriter.writeInt32("totalXp", userLeveling.totalXp());
            bsonWriter.writeBoolean("inServer", userLeveling.inServer());
        }

        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<UserLeveling> getEncoderClass() {
        return UserLeveling.class;
    }
}
