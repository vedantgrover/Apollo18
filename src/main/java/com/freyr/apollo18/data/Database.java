package com.freyr.apollo18.data;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * This class handles everything that is connected to databases.
 * THIS IS THE ONLY CLASS THAT HAS DIRECT ACCESS TO THE DATABASE
 *
 * @author Freyr
 */
public class Database {

    private final MongoCollection<Document> guildData; // The collection of documents for guilds
    private final MongoCollection<Document> userData; // The collection of documents for users
    private final MongoCollection<Document> businessData;
    private final MongoCollection<Document> transactionData;

    /**
     * Creates a connection to the database and the collections
     *
     * @param srv The connection string
     */
    public Database(String srv) {
        MongoClient mongoClient = new MongoClient(new MongoClientURI(srv));
        MongoDatabase database = mongoClient.getDatabase("apollo");

        guildData = database.getCollection("guildData");
        userData = database.getCollection("userData");
        businessData = database.getCollection("businesses");
        transactionData = database.getCollection("transactions");
    }

    public void createGuildData(Guild guild) {
        if (checkIfGuildExists(guild)) return;
        Document levelingData = new Document("onOff", true).append("channel", null).append("levelingMessage", "Congratulations [member], you have leveled up to [level]!");
        Document greetingData = new Document("onOff", false).append("welcomeChannel", null).append("leaveChannel", null).append("memberCountChannel", null).append("welcomeMessage", "[member] has joined [server]!").append("leaveMessage", "[member] has left [server].");

        guildData.insertOne(new Document("guildID", guild.getId()).append("leveling", levelingData).append("greetings", greetingData));
    }

    public boolean createUserData(User user) {
        if (user.isBot()) return false;
        if (checkIfUserExists(user)) {
            return false;
        }

        List<Document> xp = new ArrayList<>();

        List<Document> items = new ArrayList<>();
        List<Document> playlists = new ArrayList<>();

        Document economyData = new Document("balance", 0).append("bank", 0).append("job", new Document("business", null).append("job", null)).append("card", new Document("debit-card", false).append("credit-card", new Document("hasCard", false).append("currentBalance", 0).append("totalBalance", 0).append("expirationDate", null))).append("items", items);
        Document musicData = new Document("playlists", playlists);

        userData.insertOne(new Document("userID", user.getId()).append("leveling", xp).append("economy", economyData).append("music", musicData));

        return true;
    }

    public FindIterable<Document> getAllUsers() {
        return userData.find();
    }

    public FindIterable<Document> getAllGuilds() {
        return guildData.find();
    }

    public Document getUser(String userId) {
        return userData.find(new Document("userID", userId)).first();
    }

    public Document getGuild(String guildId) {
        return guildData.find(new Document("guildID", guildId)).first();
    }

    private boolean checkIfUserExists(User user) {
        FindIterable<Document> iterable = userData.find(new Document("userID", user.getId()));
        return iterable.first() != null;
    }

    private boolean checkIfGuildExists(Guild guild) {
        FindIterable<Document> iterable = guildData.find(new Document("guildID", guild.getId()));
        return iterable.first() != null;
    }

    public void updateServerStatus(String userId, String guildId, boolean l) {
        Document query = new Document("userID", userId);

        UpdateOptions options = new UpdateOptions().arrayFilters(List.of(Filters.eq("ele.guildID", guildId)));

        Bson updates = Updates.set("leveling.$[ele].inServer", l);

        userData.updateOne(query, updates, options);
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
    // region
    public void createLevelingProfile(String userId, String guildId) {
        if (checkIfUserXpExists(userId, guildId)) {
            return;
        }

        Document query = new Document("userID", userId);

        Document newXPData = new Document("guildID", guildId).append("xp", 0).append("level", 1).append("totalXp", 0).append("inServer", true);

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

        Bson update = Updates.combine(Updates.inc("leveling.$[ele].xp", xpIncrease), Updates.inc("leveling.$[ele].totalXp", xpIncrease));

        try {
            userData.updateOne(filter, update, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public void levelUp(String userId, String guildId, int bytesAdded) {
        Bson filter = Filters.and(Filters.eq("userID", userId));
        UpdateOptions options = new UpdateOptions().arrayFilters(List.of(Filters.eq("ele.guildID", guildId)));

        Bson update = Updates.combine(Updates.inc("leveling.$[ele].level", 1), Updates.set("leveling.$[ele].xp", 0));

        int oldBal = getBalance(userId);

        addBytes(userId, bytesAdded);

        try {
            userData.updateOne(filter, update, options);
            createTransaction(userId, "Leveling / Level-up", oldBal, getBalance(userId));
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public FindIterable<Document> getLevelingLeaderboard(String guildId, int limit) {
        return userData.find(new Document("leveling.guildID", guildId)).sort(Sorts.descending("leveling.totalXp")).limit(limit);
    }
    // endregion

    // Economy System
    // region
    public Document getEconomyUser(String userId) {
        return userData.find(new Document("userID", userId)).first().get("economy", Document.class);
    }
    public int getBalance(String userId) {
        return userData.find(new Document("userID", userId)).first().get("economy", Document.class).getInteger("balance");
    }

    public int getBank(String userId) {
        return userData.find(new Document("userID", userId)).first().get("economy", Document.class).getInteger("bank");
    }

    public int getNetWorth(String userId) {
        return getBalance(userId) + getBank(userId);
    }

    public void addBytes(String userId, int amount) {
        Document query = new Document("userID", userId);

        Bson updates = Updates.inc("economy.balance", amount);
        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            userData.updateOne(query, updates, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public void removeBytes(String userId, int amount) {
        addBytes(userId, -amount);
    }

    public void depositBytes(String userId, int amount) {
        removeBytes(userId, amount);

        Document query = new Document("userID", userId);

        Bson updates = Updates.inc("economy.bank", amount);

        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            userData.updateOne(query, updates, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public void withdrawBytes(String userId, int amount) {
        addBytes(userId, amount);

        Document query = new Document("userID", userId);

        Bson updates = Updates.inc("economy.bank", -amount);

        UpdateOptions options = new UpdateOptions().upsert(true);

        userData.updateOne(query, updates, options);
    }

    public AggregateIterable<Document> getEconomyLeaderboard(String guildId, int limit) {
        return userData.aggregate(Arrays.asList(Aggregates.match(Filters.and(Filters.eq("leveling.guildID", guildId), Filters.eq("leveling.inServer", true))), Aggregates.addFields(new Field("sum", Filters.eq("$add", Arrays.asList("balance", "$bank")))), Aggregates.sort(Sorts.descending("sum"))));
    }
    // endregion

    // Transactions
    // region

    public void createTransaction(String userId, String transactionType, int oldBal, int newBal) {
        Document transaction = new Document("userID", userId).append("byteExchange", (newBal - oldBal)).append("previousBal", oldBal).append("newBal", newBal).append("transactionType", transactionType).append("transactionDate", DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm:ss").format(LocalDateTime.now()));

        transactionData.insertOne(transaction);
    }

    // endregion

    // Music
    // region
    public void createPlaylist(String userId, String playlistName) {
        Document query = new Document("userID", userId);

        Document data = new Document("playlistName", playlistName.toLowerCase()).append("songs", new ArrayList<Document>());

        Bson updates = Updates.push("music.playlists", data);

        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            userData.updateOne(query, updates, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public void deletePlaylist(String userId, String playlistName) {
        Document query = new Document("userID", userId);

        Bson updates = Updates.pull("music.playlists", playlistName.toLowerCase());

        UpdateOptions options = new UpdateOptions().upsert(true);

        userData.updateOne(query, updates, options);
    }

    public void addSong(String userId, String playlist, AudioTrack song) {
        Bson filter = Filters.and(Filters.eq("userID", userId));
        UpdateOptions options = new UpdateOptions().arrayFilters(List.of(Filters.eq("ele.playlistName", playlist)));

        Document data = new Document("songName", song.getInfo().title).append("uri", song.getInfo().uri).append("position", getSongs(userId, playlist).size());

        Bson update = Updates.push("music.playlists.$[ele].songs", data);

        try {
            userData.updateOne(filter, update, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public boolean removeSong(String userId, String playlist, String songName) {
        if (!checkIfSongExists(userId, playlist, songName)) {
            return false;
        }

        Bson filter = Filters.and(Filters.eq("userID", userId));
        UpdateOptions options = new UpdateOptions().arrayFilters(List.of(Filters.eq("ele.playlistName", playlist)));

        Bson update = Updates.pull("music.playlists.$[ele].songs", new Document("songName", songName));

        try {
            userData.updateOne(filter, update, options);
        } catch (MongoException me) {
            me.printStackTrace();
            return false;
        }

        return true;
    }

    public void moveSong(String userId, String playlist, String songName, int newPos) {
        if (!checkIfSongExists(userId, playlist, songName)) {
            return;
        }

        Document query = new Document("userID", userId);
        UpdateOptions options = new UpdateOptions().arrayFilters(List.of(Filters.eq("ele.playlistName", playlist)));

        Bson updates = Updates.set("music.playlists.$[ele].position", newPos);

        try {
            userData.updateOne(query, updates, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public List<Document> getPlaylists(String userId) {
        return userData.find(new Document("userID", userId)).first().get("music", Document.class).getList("playlists", Document.class);
    }

    public List<Document> getSongs(String userId, String playlist) {
        Document userDoc = userData.find(new Document("userID", userId)).first();
        Document userPlaylist = null;
        for (Document doc : userDoc.get("music", Document.class).getList("playlists", Document.class)) {
            if (doc.getString("playlistName").equals(playlist)) {
                userPlaylist = doc;
                break;
            }
        }
        ;

        return userPlaylist.getList("songs", Document.class);
    }

    private boolean checkIfSongExists(String userId, String playlist, String songName) {
        return userData.find(new Document("userID", userId).append("music.playlists.playlistName", playlist).append("music.playlists.songs.songName", songName)).first() != null;
    }

    // endregion

    // Businesses
    // region
    public void createBusiness(String businessName, String ownerId) {

    }
    // endregion
}
