package com.freyr.apollo18.data.codec.user.economy;

import com.freyr.apollo18.data.records.user.economy.UserJob;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class UserJobCodec implements Codec<UserJob> {
    @Override
    public UserJob decode(BsonReader bsonReader, DecoderContext decoderContext) {
        bsonReader.readStartDocument();

        String businessCode = bsonReader.readString("business");
        String jobName = bsonReader.readString("job");
        int daysWorked = bsonReader.readInt32("daysWorked");
        int daysMissed = bsonReader.readInt32("daysMissed");
        boolean worked = bsonReader.readBoolean("worked");

        bsonReader.readEndDocument();

        return new UserJob(businessCode, jobName, daysWorked, daysMissed, worked);
    }

    @Override
    public void encode(BsonWriter bsonWriter, UserJob userJob, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();

        if (userJob != null) {
            bsonWriter.writeString("business", userJob.businessCode());
            bsonWriter.writeString("job", userJob.jobName());
            bsonWriter.writeInt32("daysWorked", userJob.daysWorked());
            bsonWriter.writeInt32("daysMissed", userJob.daysMissed());
            bsonWriter.writeBoolean("worked", userJob.worked());
        }

        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<UserJob> getEncoderClass() {
        return UserJob.class;
    }
}
