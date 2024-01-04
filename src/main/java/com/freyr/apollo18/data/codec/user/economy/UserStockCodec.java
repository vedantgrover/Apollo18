package com.freyr.apollo18.data.codec.user.economy;

import com.freyr.apollo18.data.records.user.economy.UserStock;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class UserStockCodec implements Codec<UserStock> {
    @Override
    public UserStock decode(BsonReader bsonReader, DecoderContext decoderContext) {
        bsonReader.readStartDocument();

        String _id = bsonReader.readString("_id");
        String stockCode = bsonReader.readString("stockCode");
        int purchasePrice = bsonReader.readInt32("purchasePrice");
        int quantity = bsonReader.readInt32("quantity");

        bsonReader.readEndDocument();

        return new UserStock(_id, stockCode, purchasePrice, quantity);
    }

    @Override
    public void encode(BsonWriter bsonWriter, UserStock userStock, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();

        if (userStock != null) {
            bsonWriter.writeString("_id", userStock._id());
            bsonWriter.writeString("stockCode", userStock.stockCode());
            bsonWriter.writeInt32("purchasePrice", userStock.purchasePrice());
            bsonWriter.writeInt32("quantity", userStock.quantity());
        }

        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<UserStock> getEncoderClass() {
        return UserStock.class;
    }
}
