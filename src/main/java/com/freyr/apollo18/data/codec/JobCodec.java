package com.freyr.apollo18.data.codec;

import com.freyr.apollo18.data.records.business.Job;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class JobCodec implements Codec<Job> {
    @Override
    public Job decode(BsonReader bsonReader, DecoderContext decoderContext) {
        bsonReader.readStartDocument();

        String name = bsonReader.readString("name");
        String description = bsonReader.readString("description");
        int salary = bsonReader.readInt32("salary");
        int daysBeforeFire = bsonReader.readInt32("daysBeforeFire");
        boolean available = bsonReader.readBoolean("available");

        bsonReader.readEndDocument();

        return new Job(name, description, salary, daysBeforeFire, available);
    }

    @Override
    public void encode(BsonWriter bsonWriter, Job job, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();

        bsonWriter.writeString("name", job.name());
        bsonWriter.writeString("description", job.description());
        bsonWriter.writeInt32("salary", job.salary());
        bsonWriter.writeInt32("daysBeforeFire", job.daysBeforeFire());
        bsonWriter.writeBoolean("available", job.available());

        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<Job> getEncoderClass() {
        return Job.class;
    }
}
