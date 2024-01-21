package com.freyr.apollo18.data.codec.business;

import com.freyr.apollo18.data.records.business.Business;
import com.freyr.apollo18.data.records.business.Job;
import com.freyr.apollo18.data.records.business.Stock;
import org.bson.BsonDocument;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.ArrayList;
import java.util.List;

public class BusinessCodec implements Codec<Business> {
    private final Codec<Job> jobCodec;
    private final Codec<Stock> stockCodec;

    public BusinessCodec(CodecRegistry registry) {
        this.jobCodec = registry.get(Job.class);
        this.stockCodec = registry.get(Stock.class);
    }

    @Override
    public Business decode(BsonReader bsonReader, DecoderContext decoderContext) {
        bsonReader.readStartDocument();

        if (bsonReader.readName().equals("_id")) {
            bsonReader.skipValue();
        }
        String name = bsonReader.readString("name");
        String stockCode = bsonReader.readString("stockCode");
        String owner = bsonReader.readString("owner");
        String description = bsonReader.readString("description");
        String logo = bsonReader.readString("logo");
        boolean isPublic = bsonReader.readBoolean("public");

        List<Job> jobs = readJobs(bsonReader, decoderContext);

        Stock stock = null;
        if (bsonReader.readName().equals("stock")) {
            stock = stockCodec.decode(bsonReader, decoderContext);
        }

        bsonReader.readEndDocument();
        return new Business(name, stockCode, owner, description, logo, isPublic, jobs, stock);
    }

    @Override
    public void encode(BsonWriter bsonWriter, Business business, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();

        if (business != null) {
            bsonWriter.writeString("name", business.name());
            bsonWriter.writeString("stockCode", business.stockCode());
            bsonWriter.writeString("owner", business.owner());
            bsonWriter.writeString("description", business.description());
            bsonWriter.writeString("logo", business.logo());
            bsonWriter.writeBoolean("isPublic", business.isPublic());

            writeJobs(bsonWriter, business.jobs(), encoderContext);

            bsonWriter.writeStartDocument("stock");
            stockCodec.encode(bsonWriter, business.stock(), encoderContext);
            bsonWriter.writeEndDocument();
        }

        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<Business> getEncoderClass() {
        return Business.class;
    }

    private List<Job> readJobs(BsonReader bsonReader, DecoderContext decoderContext) {
        List<Job> jobs = new ArrayList<>();
        if (bsonReader.readBsonType() == BsonType.ARRAY) {
            bsonReader.readStartArray();
            while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                jobs.add(jobCodec.decode(bsonReader, decoderContext));
            }
            bsonReader.readEndArray();
        }
        return jobs;
    }

    private void writeJobs(BsonWriter bsonWriter, List<Job> jobs, EncoderContext encoderContext) {
        bsonWriter.writeStartArray("jobs");
        for (Job job : jobs) {
            jobCodec.encode(bsonWriter, job, encoderContext);
        }
        bsonWriter.writeEndArray();
    }
}
