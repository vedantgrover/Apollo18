package com.freyr.apollo18.data.codec.user;

import com.freyr.apollo18.data.records.user.User;
import com.freyr.apollo18.data.records.user.UserLeveling;
import com.freyr.apollo18.data.records.user.economy.UserEconomy;
import com.freyr.apollo18.data.records.user.music.UserMusic;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.ArrayList;
import java.util.List;

public class UserCodec implements Codec<User> {

    private final Codec<UserLeveling> userLevelingCodec;
    private final Codec<UserEconomy> userEconomyCodec;
    private final Codec<UserMusic> userMusicCodec;

    public UserCodec(CodecRegistry codecRegistry) {
        userLevelingCodec = codecRegistry.get(UserLeveling.class);
        userEconomyCodec = codecRegistry.get(UserEconomy.class);
        userMusicCodec = codecRegistry.get(UserMusic.class);
    }

    @Override

    public User decode(BsonReader bsonReader, DecoderContext decoderContext) {
        bsonReader.readStartDocument();

        if (bsonReader.readName().equals("_id")) {
            bsonReader.skipValue();
        }

        String userID = bsonReader.readString("userID");
        List<UserLeveling> leveling = readLeveling(bsonReader, decoderContext);

        UserEconomy userEconomy = null;
        if (bsonReader.readName().equals("economy")) {
            userEconomy = userEconomyCodec.decode(bsonReader, decoderContext);
        }

        UserMusic userMusic = null;
        if (bsonReader.readName().equals("music")) {
            userMusic = userMusicCodec.decode(bsonReader, decoderContext);
        }

        boolean notifications = bsonReader.readBoolean("notifications");

        bsonReader.readEndDocument();

        return new User(userID, leveling, userEconomy, userMusic, notifications);
    }

    @Override
    public void encode(BsonWriter bsonWriter, User user, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();

        if (user != null) {
            bsonWriter.writeString("userID", user.userID());

            writeLeveling(bsonWriter, user.leveling(), encoderContext);

            bsonWriter.writeName("economy");
            userEconomyCodec.encode(bsonWriter, user.economy(), encoderContext);

            bsonWriter.writeName("music");
            userMusicCodec.encode(bsonWriter, user.music(), encoderContext);

            bsonWriter.writeBoolean("notifications", user.notifications());
        }

        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<User> getEncoderClass() {
        return null;
    }

    private List<UserLeveling> readLeveling(BsonReader bsonReader, DecoderContext decoderContext) {
        List<UserLeveling> levelings = new ArrayList<>();
        if (bsonReader.readBsonType() == BsonType.ARRAY) {
            bsonReader.readStartArray();
            while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                levelings.add(userLevelingCodec.decode(bsonReader, decoderContext));
            }
            bsonReader.readEndArray();
        }
        return levelings;
    }

    private void writeLeveling(BsonWriter bsonWriter, List<UserLeveling> userLevelings, EncoderContext encoderContext) {
        bsonWriter.writeStartArray("userLeveling");
        for (UserLeveling leveling : userLevelings) {
            userLevelingCodec.encode(bsonWriter, leveling, encoderContext);
        }
        bsonWriter.writeEndArray();
    }
}
