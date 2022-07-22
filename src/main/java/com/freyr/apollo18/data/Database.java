package com.freyr.apollo18.data;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class Database {

    private final MongoCollection<Document> guildData;
    private final MongoCollection<Document> userData;

    public Database(String srv) {
        MongoClient mongoClient = new MongoClient(new MongoClientURI(srv));
        MongoDatabase database = mongoClient.getDatabase("apollo");

        guildData = database.getCollection("guildData");
        userData = database.getCollection("userData");
    }

    public void createGuildData(Guild guild) {
        Document levelingData = new Document("onOff", true).append("channel", null).append("levelingMessage", "Congratulations [member], you have leveled up to [level]!");
        Document greetingData = new Document("onOff", false).append("welcomeChannel", null).append("leaveChannel", null).append("memberCountChannel", null).append("welcomeMessage", "[member] has joined [server]!").append("leaveMessage", "[member] has left [server].");

        guildData.insertOne(
                new Document("guildID", guild.getIdLong()).append("leveling", levelingData).append("greetings", greetingData)
        );
    }

    public boolean createUserData(User user) {
        List<Document> items = new ArrayList<>();
        List<Document> playlists = new ArrayList<>();
        List<Document> songs = new ArrayList<>();

        Document levelingData = new Document("xp", 0);
        Document economyData = new Document("balance", 0).append("bank", 0).append("job", new Document("business", null).append("job", null)).append("card", new Document("debit-card", false).append("credit-card", new Document("hasCard", false).append("currentBalance", 0).append("totalBalance", 0).append("expirationDate", null))).append("items", items);
        Document musicData = new Document("playlists", playlists);

        if (checkIfUserExists(user)) {
            return false;
        }

        userData.insertOne(
                new Document("userID", user.getIdLong()).append("leveling", levelingData).append("economy", economyData).append("music", musicData)
        );

        return true;
    }

    private boolean checkIfUserExists(User user) {
        FindIterable<Document> iterable = userData.find(new Document("userID", user.getIdLong()));
        return iterable.first() != null;
    }

    // Welcome System
    // region
    public boolean getWelcomeSystemToggle(long guildId) {
        return guildData.find(new Document("guildID", guildId)).first().get("greetings", Document.class).getBoolean("onOff");
    }

    public long getWelcomeChannel(long guildId) {
        return guildData.find(new Document("guildID", guildId)).first().get("greetings", Document.class).getLong("welcomeChannel");
    }

    public long getLeaveChannel(long guildId) {
        return guildData.find(new Document("guildID", guildId)).first().get("greetings", Document.class).getLong("leaveChannel");
    }

    public String getWelcomeMessage(long guildId) {
        return guildData.find(new Document("guildID", guildId)).first().get("greetings", Document.class).getString("welcomeMessage");
    }

    public String getLeaveMessage(long guildId) {
        return guildData.find(new Document("guildID", guildId)).first().get("greetings", Document.class).getString("leaveMessage");
    }

    public long getMemberCountChannel(long guildId) {
        return guildData.find(new Document("guildID", guildId)).first().get("greetings", Document.class).getLong("memberCountChannel");
    }

    public void toggleWelcomeSystem(long guildId) {
        Document query = new Document("guildID", guildId);

        Bson updates = Updates.set("greetings.onOff", !getWelcomeSystemToggle(guildId));

        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            guildData.updateOne(query, updates, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public void setWelcomeChannel(long guildId, long channelId) {
        Document query = new Document("guildID", guildId);

        Bson updates = Updates.set("greetings.welcomeChannel", channelId);

        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            guildData.updateOne(query, updates, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public void setLeaveChannel(long guildId, long channelId) {
        Document query = new Document("guildID", guildId);

        Bson updates = Updates.set("greetings.leaveChannel", channelId);

        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            guildData.updateOne(query, updates, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public void setMemberCountChannel(long guildId, long channelId) {
        Document query = new Document("guildID", guildId);

        Bson updates = Updates.set("greetings.memberCountChannel", channelId);

        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            guildData.updateOne(query, updates, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public void setWelcomeMessage(long guildId, String message) {
        Document query = new Document("guildID", guildId);

        Bson updates = Updates.set("greetings.welcomeMessage", message);

        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            guildData.updateOne(query, updates, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public void setLeaveMessage(long guildId, String message) {
        Document query = new Document("guildID", guildId);

        Bson updates = Updates.set("greetings.leaveMessage", message);

        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            guildData.updateOne(query, updates, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public void resetWelcomeSystem(long guildId) {
        Document query = new Document("guildID", guildId);

        Bson updates = Updates.combine(
                Updates.set("greetings.onOff", false),
                Updates.set("greetings.welcomeChannel", null),
                Updates.set("greetings.leaveChannel", null),
                Updates.set("greetings.memberCountChannel", null),
                Updates.set("greetings.welcomeMessage", "[member] has joined [server]!"),
                Updates.set("greetings.leaveMessage", "[member] has left [server].")
        );

        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            guildData.updateOne(query, updates, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }
    // endregion
}
