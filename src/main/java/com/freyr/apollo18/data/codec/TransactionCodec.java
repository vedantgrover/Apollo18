package com.freyr.apollo18.data.codec;

import com.freyr.apollo18.data.records.Transaction;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class TransactionCodec implements Codec<Transaction> {
    @Override
    public Transaction decode(BsonReader bsonReader, DecoderContext decoderContext) {
        bsonReader.readStartDocument();

        String userID = bsonReader.readString("userID");
        int byteExchange = bsonReader.readInt32("byteExchange");
        int previousBal = bsonReader.readInt32("previousBal");
        int newBal = bsonReader.readInt32("newBal");
        String transactionType = bsonReader.readString("transactionType");
        String transactionDate = bsonReader.readString("transactionDate");

        bsonReader.readEndDocument();

        return new Transaction(userID, byteExchange, previousBal, newBal, transactionType, transactionDate);
    }

    @Override
    public void encode(BsonWriter bsonWriter, Transaction transaction, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();

        if (transaction != null) {
            bsonWriter.writeString("userID", transaction.userID());
            bsonWriter.writeInt32("byteExchange", transaction.byteExchange());
            bsonWriter.writeInt32("previousBal", transaction.previousBal());
            bsonWriter.writeInt32("newBal", transaction.newBal());
            bsonWriter.writeString("transactionType", transaction.transactionType());
            bsonWriter.writeString("transactionDate", transaction.transactionDate());
        }

        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<Transaction> getEncoderClass() {
        return Transaction.class;
    }
}
