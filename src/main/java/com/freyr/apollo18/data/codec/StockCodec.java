package com.freyr.apollo18.data.codec;

import com.freyr.apollo18.data.records.business.Stock;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class StockCodec implements Codec<Stock> {

    @Override
    public Stock decode(BsonReader bsonReader, DecoderContext decoderContext) {
        bsonReader.readStartDocument();

        String ticker = bsonReader.readString("ticker");
        int currentPrice = bsonReader.readInt32("currentPrice");
        int previousPrice = bsonReader.readInt32("previousPrice");
        int change = bsonReader.readInt32("change");
        String arrowEmoji = bsonReader.readString("arrowEmoji");

        bsonReader.readEndDocument();

        return new Stock(ticker, currentPrice, previousPrice, change, arrowEmoji);
    }

    @Override
    public void encode(BsonWriter bsonWriter, Stock stock, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();

        bsonWriter.writeString("ticker", stock.ticker());
        bsonWriter.writeInt32("currentPrice", stock.currentPrice());
        bsonWriter.writeInt32("previousPrice", stock.previousPrice());
        bsonWriter.writeInt32("change", stock.change());
        bsonWriter.writeString("arrowEmoji", stock.arrowEmoji());

        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<Stock> getEncoderClass() {
        return Stock.class;
    }
}
