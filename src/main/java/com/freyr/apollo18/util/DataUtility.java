package com.freyr.apollo18.util;

import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;

public class DataUtility {

    public static String readNullableString(BsonReader bsonReader, String fieldName) {
        try {
            return bsonReader.readString(fieldName);
        } catch (BsonInvalidOperationException invalidOperationException) {
//            System.err.println(invalidOperationException);
            bsonReader.readNull();
            return null;
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    public static void writeNullableString(BsonWriter writer, String fieldName, String value) {
        if (value == null) {
            writer.writeNull(fieldName);
        } else {
            writer.writeString(fieldName, value);
        }
    }
}
