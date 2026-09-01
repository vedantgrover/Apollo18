package com.freyr.apollo18.util;

import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataUtility {

    private static final Logger logger = LoggerFactory.getLogger(DataUtility.class);

    public static String readNullableString(BsonReader bsonReader, String fieldName) {
        try {
            return bsonReader.readString(fieldName);
        } catch (BsonInvalidOperationException invalidOperationException) {
            bsonReader.readNull();
            return null;
        } catch (Exception e) {
            logger.error("Error reading nullable string for field {}", fieldName, e);
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
