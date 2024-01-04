package com.freyr.apollo18.data.codec.user.economy;

import com.freyr.apollo18.data.records.user.economy.UserCard;
import com.freyr.apollo18.data.records.user.economy.UserEconomy;
import com.freyr.apollo18.data.records.user.economy.UserJob;
import com.freyr.apollo18.data.records.user.economy.UserStock;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.ArrayList;
import java.util.List;

public class UserEconomyCodec implements Codec<UserEconomy> {

    private final Codec<UserCard> userCardCodec;
    private final Codec<UserJob> userJobCodec;
    private final Codec<UserStock> userStockCodec;

    public UserEconomyCodec(CodecRegistry codecRegistry) {
        userCardCodec = codecRegistry.get(UserCard.class);
        userJobCodec = codecRegistry.get(UserJob.class);
        userStockCodec = codecRegistry.get(UserStock.class);
    }

    @Override
    public UserEconomy decode(BsonReader bsonReader, DecoderContext decoderContext) {
        bsonReader.readStartDocument();

        int balance = bsonReader.readInt32("balance");
        int bank = bsonReader.readInt32("bank");

        UserJob job = null;
        if (bsonReader.readName().equals("job")) {
            job = userJobCodec.decode(bsonReader, decoderContext);
        }

        UserCard userCard = null;
        if (bsonReader.readName().equals("card")) {
            userCard = userCardCodec.decode(bsonReader, decoderContext);
        }

        if (bsonReader.readName().equals("items")) {
            skipArray(bsonReader);
        }

        List<UserStock> stocks = readStocks(bsonReader, decoderContext);

        bsonReader.readEndDocument();

        return new UserEconomy(balance, bank, job, userCard, stocks);
    }

    @Override
    public void encode(BsonWriter bsonWriter, UserEconomy economy, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();

        if (economy != null) {
            bsonWriter.writeInt32("balance", economy.balance());
            bsonWriter.writeInt32("bank", economy.bank());

            bsonWriter.writeName("job");
            userJobCodec.encode(bsonWriter, economy.job(), encoderContext);

            bsonWriter.writeName("card");
            userCardCodec.encode(bsonWriter, economy.card(), encoderContext);

            bsonWriter.writeName("stocks");
            writeStocks(bsonWriter, economy.stocks(), encoderContext);
        }

        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<UserEconomy> getEncoderClass() {
        return UserEconomy.class;
    }

    private List<UserStock> readStocks(BsonReader bsonReader, DecoderContext decoderContext) {
        List<UserStock> stocks = new ArrayList<>();
        if (bsonReader.readBsonType() == BsonType.ARRAY) {
            bsonReader.readStartArray();
            while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                stocks.add(userStockCodec.decode(bsonReader, decoderContext));
            }
            bsonReader.readEndArray();
        }
        return stocks;
    }

    private void writeStocks(BsonWriter bsonWriter, List<UserStock> stocks, EncoderContext encoderContext) {
        bsonWriter.writeStartArray("stocks");
        for (UserStock stock : stocks) {
            userStockCodec.encode(bsonWriter, stock, encoderContext);
        }
        bsonWriter.writeEndArray();
    }

    private void skipArray(BsonReader reader) {
        reader.readStartArray();

        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            reader.skipValue();
        }

        reader.readEndArray();
    }
}
