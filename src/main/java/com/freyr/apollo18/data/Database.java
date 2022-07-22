package com.freyr.apollo18.data;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
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

        guildData.insertOne(new Document("guildID", guild.getId()).append("leveling", levelingData).append("greetings", greetingData));
    }

    public boolean createUserData(User user) {
        if (user.isBot()) return false;
        List<Document> xp = new ArrayList<>();

        List<Document> items = new ArrayList<>();
        List<Document> playlists = new ArrayList<>();
        List<Document> songs = new ArrayList<>();

        Document economyData = new Document("balance", 0).append("bank", 0).append("job", new Document("business", null).append("job", null)).append("card", new Document("debit-card", false).append("credit-card", new Document("hasCard", false).append("currentBalance", 0).append("totalBalance", 0).append("expirationDate", null))).append("items", items);
        Document musicData = new Document("playlists", playlists);

        if (checkIfUserExists(user)) {
            return false;
        }

        userData.insertOne(new Document("userID", user.getId()).append("leveling", xp).append("economy", economyData).append("music", musicData));

        return true;
    }

    private boolean checkIfUserExists(User user) {
        FindIterable<Document> iterable = userData.find(new Document("userID", user.getId()));
        return iterable.first() != null;
    }

    // Welcome System
    // region
    public boolean getWelcomeSystemToggle(String guildId) {
        return guildData.find(new Document("guildID", guildId)).first().get("greetings", Document.class).getBoolean("onOff");
    }

    public String getWelcomeChannel(String guildId) {
        return guildData.find(new Document("guildID", guildId)).first().get("greetings", Document.class).getString("welcomeChannel");
    }

    public String getLeaveChannel(String guildId) {
        return guildData.find(new Document("guildID", guildId)).first().get("greetings", Document.class).getString("leaveChannel");
    }

    public String getWelcomeMessage(String guildId) {
        return guildData.find(new Document("guildID", guildId)).first().get("greetings", Document.class).getString("welcomeMessage");
    }

    public String getLeaveMessage(String guildId) {
        return guildData.find(new Document("guildID", guildId)).first().get("greetings", Document.class).getString("leaveMessage");
    }

    public String getMemberCountChannel(String guildId) {
        return guildData.find(new Document("guildID", guildId)).first().get("greetings", Document.class).getString("memberCountChannel");
    }

    public void toggleWelcomeSystem(String guildId) {
        Document query = new Document("guildID", guildId);

        Bson updates = Updates.set("greetings.onOff", !getWelcomeSystemToggle(guildId));

        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            guildData.updateOne(query, updates, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public void setWelcomeChannel(String guildId, String channelId) {
        Document query = new Document("guildID", guildId);

        Bson updates = Updates.set("greetings.welcomeChannel", channelId);

        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            guildData.updateOne(query, updates, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public void setLeaveChannel(String guildId, String channelId) {
        Document query = new Document("guildID", guildId);

        Bson updates = Updates.set("greetings.leaveChannel", channelId);

        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            guildData.updateOne(query, updates, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public void setMemberCountChannel(String guildId, String channelId) {
        Document query = new Document("guildID", guildId);

        Bson updates = Updates.set("greetings.memberCountChannel", channelId);

        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            guildData.updateOne(query, updates, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public void setWelcomeMessage(String guildId, String message) {
        Document query = new Document("guildID", guildId);

        Bson updates = Updates.set("greetings.welcomeMessage", message);

        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            guildData.updateOne(query, updates, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public void setLeaveMessage(String guildId, String message) {
        Document query = new Document("guildID", guildId);

        Bson updates = Updates.set("greetings.leaveMessage", message);

        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            guildData.updateOne(query, updates, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public void resetWelcomeSystem(String guildId) {
        Document query = new Document("guildID", guildId);

        Bson updates = Updates.combine(Updates.set("greetings.onOff", false), Updates.set("greetings.welcomeChannel", null), Updates.set("greetings.leaveChannel", null), Updates.set("greetings.memberCountChannel", null), Updates.set("greetings.welcomeMessage", "[member] has joined [server]!"), Updates.set("greetings.leaveMessage", "[member] has left [server]."));

        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            guildData.updateOne(query, updates, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }
    // endregion

    // Leveling System
    public void createLevelingProfile(String userId, String guildId) {
        if (checkIfUserXpExists(userId, guildId)) {
            return;
        }

        Document query = new Document("userID", userId);

        Document newXPData = new Document("guildID", guildId).append("xp", 0).append("level", 1).append("totalXp", 0);

        Bson updates = Updates.push("leveling", newXPData);

        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            userData.updateOne(query, updates, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    private boolean checkIfUserXpExists(String userId, String guildId) {
        Document userDoc = userData.find(new Document("userID", userId)).first();

        List<Document> xp = userDoc.getList("leveling", Document.class);

        for (Document doc : xp) {
            if (doc.getString("guildID").equals(guildId)) {
                return true;
            }
        }

        return false;
    }

    public boolean getLevelingSystemToggle(String guildId) {
        return guildData.find(new Document("guildID", guildId)).first().get("leveling", Document.class).getBoolean("onOff");
    }

    public void toggleLevelingSystem(String guildId) {
        Document query = new Document("guildID", guildId);

        Bson updates = Updates.set("leveling.onOff", !getLevelingSystemToggle(guildId));

        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            guildData.updateOne(query, updates, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public String getLevelingChannel(String guildId) {
        return guildData.find(new Document("guildID", guildId)).first().get("leveling", Document.class).getString("channel");
    }

    public void setLevelingChannel(String guildId, String channelId) {
        Document query = new Document("guildID", guildId);

        Bson updates = Updates.set("leveling.channel", channelId);

        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            guildData.updateOne(query, updates, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public String getLevelingMessage(String guildId) {
        return guildData.find(new Document("guildID", guildId)).first().get("leveling", Document.class).getString("levelingMessage");
    }

    public void setLevelingMessage(String guildId, String message) {
        Document query = new Document("guildID", guildId);

        Bson updates = Updates.set("leveling.levelingMessage", message);

        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            guildData.updateOne(query, updates, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public Document getUserLevelingProfile(String userId, String guildId) {
        Document userDoc = userData.find(new Document("userID", userId)).first();
        Document guildUserXpData = null;
        for (Document doc : userDoc.getList("leveling", Document.class)) {
            if (doc.getString("guildID").equals(guildId)) {
                guildUserXpData = doc;
                break;
            }
        }

        return guildUserXpData;
    }

    public void addXptoUser(String userId, String guildId) {
        Bson filter = Filters.and(Filters.eq("userID", userId));
        UpdateOptions options = new UpdateOptions().arrayFilters(List.of(Filters.eq("ele.guildID", guildId)));

        int xpIncrease = (int) (Math.random() * ((15 - 10) + 1)) + 10;

        Bson update = Updates.combine(
                Updates.inc("leveling.$[ele].xp", xpIncrease),
                Updates.inc("leveling.$[ele].totalXp", xpIncrease)
        );

        try {
            userData.updateOne(filter, update, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public void levelUp(String userId, String guildId) {
        Bson filter = Filters.and(Filters.eq("userID", userId));
        UpdateOptions options = new UpdateOptions().arrayFilters(List.of(Filters.eq("ele.guildID", guildId)));

        Bson update = Updates.combine(
                Updates.inc("leveling.$[ele].level", 1),
                Updates.set("leveling.$[ele].xp", 0)
        );

        try {
            userData.updateOne(filter, update, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }
}
