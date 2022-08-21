package com.freyr.apollo18.data;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.handlers.BusinessHandler;
import com.freyr.apollo18.util.textFormatters.RandomString;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.lang.Nullable;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class handles everything that is connected to databases.
 * THIS IS THE ONLY CLASS THAT HAS DIRECT ACCESS TO THE DATABASE
 *
 * @author Freyr
 */
public class Database {

    private final Apollo18 bot;

    private final MongoCollection<Document> guildData; // The collection of documents for guilds
    private final MongoCollection<Document> userData; // The collection of documents for users
    private final MongoCollection<Document> businessData;
    private final MongoCollection<Document> transactionData;

    /**
     * Creates a connection to the database and the collections
     *
     * @param srv The connection string
     */
    public Database(String srv, Apollo18 bot) {
        this.bot = bot;
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

        Document economyData = new Document("balance", 0).append("bank", 0).append("job", new Document("business", null).append("job", null).append("daysWorked", 0).append("daysMissed", 0).append("worked", false)).append("card", new Document("debit-card", false).append("credit-card", new Document("hasCard", false).append("currentBalance", 0).append("totalBalance", 0).append("expirationDate", null))).append("items", items);
        Document musicData = new Document("playlists", playlists);

        userData.insertOne(new Document("userID", user.getId()).append("notifications", true).append("leveling", xp).append("economy", economyData).append("music", musicData));

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
    public void createDefaultBusiness(String businessName, String businessDescription, String ticker, String stockCode, @Nullable String logo) {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://twelve-data1.p.rapidapi.com/quote?symbol=" + ticker + "&interval=1day&outputsize=30&format=json")).header("X-RapidAPI-Key", bot.getConfig().get("RAPIDAPI_KEY", System.getenv("RAPIDAPI_KEY"))).header("X-RapidAPI-Host", "twelve-data1.p.rapidapi.com").method("GET", HttpRequest.BodyPublishers.noBody()).build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject data = new JSONObject(response.body());

            int change = (int) Double.parseDouble(data.getString("change"));
            int currentPrice = (int) Double.parseDouble(data.getString("close")) / 4;
            int previousPrice = currentPrice - change;

            Document stockData = new Document("ticker", ticker).append("currentPrice", currentPrice).append("previousPrice", previousPrice).append("change", change).append("arrowEmoji", BusinessHandler.getArrow(change));
            Document document = new Document("name", businessName).append("stockCode", stockCode).append("owner", "default").append("description", businessDescription).append("logo", (logo == null) ? "https://library.kissclipart.com/20181224/fww/kissclipart-free-vector-building-clipart-computer-icons-66d576fc7c1dd7ff.png" : logo).append("public", true).append("jobs", new ArrayList<Document>()).append("stock", stockData);

            businessData.insertOne(document);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public Document getBusiness(String stockCode) {
        return businessData.find(new Document("stockCode", stockCode)).first();
    }

    public List<Document> getBusinesses() {
        List<Document> result = new ArrayList<>();

        FindIterable<Document> businesses = businessData.find(new Document("public", true));
        for (Document business : businesses) {
            result.add(business);
        }

        return result;
    }

    public void addStockToUser(Document business, String userId, int quantity) {
        Document userBusiness = new Document("_id", new RandomString(12).nextString()).append("stockCode", business.getString("stockCode")).append("purchasePrice", business.get("stock", Document.class).getInteger("currentPrice")).append("quantity", quantity);
        Document query = new Document("userID", userId);

        Bson updates = Updates.push("economy.stocks", userBusiness);

        UpdateOptions options = new UpdateOptions().upsert(true);

        int oldBal = getBalance(userId);

        userData.updateOne(query, updates, options);
        removeBytes(userId, business.get("stock", Document.class).getInteger("currentPrice") * quantity);

        createTransaction(userId, "Business / Stock / Buy", oldBal, getBalance(userId));
    }

    public void removeStockFromUser(String userId, String stockCode, int quantity) {
        List<Document> actions = getPurchasedStocks(userId, stockCode, quantity);
        Document business = getBusiness(stockCode);
        int currentPrice = business.get("stock", Document.class).getInteger("currentPrice");

        int totalPrice = quantity * currentPrice;

        for (int i = 0; i < actions.size(); i++) {
            if (actions.get(i).getInteger("action") == 0) {
                Document updates = new Document("$pull", new Document("economy.stocks", new Document("_id", actions.get(i).getString("_id"))));

                userData.updateOne(new Document("userID", userId), updates, new UpdateOptions().upsert(true));
            } else {
                UpdateOptions options = new UpdateOptions().arrayFilters(List.of(Filters.eq("ele._id", actions.get(i).getString("_id"))));
                Bson updates = Updates.set("economy.stocks.$[ele].quantity", actions.get(i).getInteger("quantity"));

                userData.updateOne(new Document("userID", userId), updates, options);
            }
        }

        addBytes(userId, totalPrice);
        createTransaction(userId, "Business / Stock / Sell", getBalance(userId) - totalPrice, getBalance(userId));
    }

    private List<Document> getPurchasedStocks(String userId, String stockCode, int quantity) {
        List<Document> result = new ArrayList<>();

        Document userDoc = userData.find(new Document("userID", userId)).first();
        List<Document> stocks = userDoc.get("economy", Document.class).getList("stocks", Document.class);
        int tempQuantity = quantity;

        System.out.println("Info: Building Document for Stock: " + stockCode + ", Quantity: " + tempQuantity);

        for (int i = 0; i < stocks.size(); i++) {
            if (stocks.get(i).getString("stockCode").equals(stockCode.toUpperCase()) && tempQuantity > 0) {
                if (stocks.get(i).getInteger("quantity") <= tempQuantity && (tempQuantity - stocks.get(i).getInteger("quantity")) >= 0) {
                    tempQuantity -= stocks.get(i).getInteger("quantity");
                    System.out.println("Info: Action: Delete | SQ: " + stocks.get(i).getInteger("quantity") + " | TQ Left: " + tempQuantity);
                    result.add(new Document("_id", stocks.get(i).getString("_id")).append("quantity", stocks.get(i).getInteger("quantity")).append("index", i).append("action", 0));
                } else if (stocks.get(i).getInteger("quantity") > tempQuantity) {
                    System.out.println("Info: Action: Update | SQ from: " + stocks.get(i).getInteger("quantity") + " to: " + (stocks.get(i).getInteger("quantity") - tempQuantity));
                    result.add(new Document("_id", stocks.get(i).getString("_id")).append("quantity", (stocks.get(i).getInteger("quantity") - tempQuantity)).append("index", i).append("action", 1));
                    tempQuantity -= tempQuantity;
                    System.out.println("Info: TQ Left: " + tempQuantity);
                }
            }
        }

        System.out.println(result);
        return result;
    }

    public int getTotalStocks(String userId, String stockCode) {
        try {
            List<Document> purchasedStocks = userData.find(new Document("userID", userId)).first().get("economy", Document.class).getList("stocks", Document.class);
            int totalStocks = 0;
            for (Document purchasedStock : purchasedStocks) {
                if (purchasedStock.getString("stockCode").equals(stockCode)) {
                    totalStocks += purchasedStock.getInteger("quantity");
                }
            }

            return totalStocks;
        } catch (Exception e) {
            return 0;
        }
    }

    public void updateStocks() {
        FindIterable<Document> businesses = businessData.find();

        for (Document business : businesses) {
            try {
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://twelve-data1.p.rapidapi.com/quote?symbol=" + business.get("stock", Document.class).getString("ticker") + "&interval=1day&outputsize=30&format=json")).header("X-RapidAPI-Key", bot.getConfig().get("RAPIDAPI_KEY", System.getenv("RAPIDAPI_KEY"))).header("X-RapidAPI-Host", "twelve-data1.p.rapidapi.com").method("GET", HttpRequest.BodyPublishers.noBody()).build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                JSONObject data = new JSONObject(response.body());

                int change = (int) Double.parseDouble(data.getString("change"));
                int currentPrice = (int) Double.parseDouble(data.getString("close")) / 4;
                int previousPrice = currentPrice - change;

                Bson updates = Updates.combine(
                        Updates.set("stock.currentPrice", currentPrice),
                        Updates.set("stock.previousPrice", previousPrice),
                        Updates.set("stock.change", change),
                        Updates.set("stock.arrowEmoji", BusinessHandler.getArrow(change))
                );

                businessData.updateOne(business, updates, new UpdateOptions().upsert(true));
                createTransaction("stockUpdate", "Business / Stock / Update", previousPrice, currentPrice);
                System.out.println("Updating " + business.get("stock", Document.class).getString("ticker") + "; Old Price: " + previousPrice + "; Current Price: " + currentPrice + "; Change: " + change + "\nAll Details: " + data);
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    // Jobs
    // region

    public void createDefaultJob(String code, String jobName, String jobDescription, int salary, int daysBeforeFire) {
        Document query = new Document("stockCode", code);
        Document job = new Document("name", jobName).append("description", jobDescription).append("salary", salary).append("daysBeforeFire", daysBeforeFire).append("available", true);

        Bson updates = Updates.push("jobs", job);

        businessData.updateOne(query, updates, new UpdateOptions().upsert(true));
    }

    public List<Document> getJobs(String code) {
        return businessData.find(new Document("stockCode", code)).first().getList("jobs", Document.class);
    }

    public Document getJob(String code, String jobName) {
        Document business = businessData.find(new Document("stockCode", code)).first();
        if (business == null) return null;
        Document job = null;
        for (Document doc : business.getList("jobs", Document.class)) {
            if (doc.getString("name").equalsIgnoreCase(jobName)) {
                job = doc;
                break;
            }
        }

        return job;
    }

    public boolean work(String userId) {
        Document userEconomyDoc = userData.find(new Document("userID", userId)).first().get("economy", Document.class);
        Document userJob = getJob(userEconomyDoc.get("job", Document.class).getString("business"), userEconomyDoc.get("job", Document.class).getString("job"));
        if (userJob == null) return false;

        addBytes(userId, userJob.getInteger("salary"));
        Document query = new Document("userID", userId);

        if (userEconomyDoc.get("job", Document.class).getInteger("daysMissed") == null) {
            Document job = new Document("business", userEconomyDoc.get("job", Document.class).getString("business")).append("job", userEconomyDoc.get("job", Document.class).getString("job")).append("daysWorked", 1).append("daysMissed", 0).append("worked", true);

            Bson updates = Updates.set("economy.job", job);

            userData.updateOne(new Document("userID", userId), updates, new UpdateOptions().upsert(true));
            return true;
        }

        if (!userEconomyDoc.get("job", Document.class).getBoolean("worked")) {
            Bson updates = Updates.combine(
                    Updates.inc("economy.job.daysWorked", 1),
                    Updates.set("economy.job.worked", true)
            );

            userData.updateOne(query, updates, new UpdateOptions().upsert(true));
            createTransaction(userId, "Job / Work", getBalance(userId) - userJob.getInteger("salary"), getBalance(userId));
        }
        return true;
    }

    public void setJob(String userId, String code, String jobName) {
        Document query = new Document("userID", userId);
        Document job = getJob(code, jobName);

        Bson updates = Updates.combine(
                Updates.set("economy.job.business", code),
                Updates.set("economy.job.job", job.getString("name"))
        );

        userData.updateOne(query, updates, new UpdateOptions().upsert(true));
    }

    public Document getUserJob(String userId) {
        return userData.find(new Document("userID", userId)).first().get("economy", Document.class).get("job", Document.class);
    }

    // endregion

    public void dailyWorkChecks() {
        for (Document user : userData.find(new Document())) {
            Document query = new Document("userID", user.getString("userID"));
            System.out.println("Updating data for " + user.getString("userID"));
            if (user.get("economy", Document.class).get("job", Document.class).getInteger("daysMissed") == null
                    || user.get("economy", Document.class).get("job", Document.class).getBoolean("worked") == null) {
                Document job = new Document("business", null).append("job", null).append("daysWorked", 0).append("daysMissed", 0).append("worked", false);
                Bson updates = Updates.set("economy.job", job);

                userData.updateOne(query, updates, new UpdateOptions().upsert(true));
                System.out.println("Replaced Job Data with Updated Job Data for " + user.getString("userID"));
            }

            if (!user.get("economy", Document.class).get("job", Document.class).getBoolean("worked") && user.get("economy", Document.class).get("job", Document.class).getString("job") != null) {
                Bson updates = Updates.combine(
                        Updates.set("economy.job.daysWorked", 0),
                        Updates.inc("economy.job.daysMissed", 1)
                );

                userData.updateOne(query, updates, new UpdateOptions().upsert(true));
                System.out.println(user.getString("userID") + " did not work today. Days missed added");
            }

            if (getJob(user.get("economy", Document.class).get("job", Document.class).getString("business"), user.get("economy", Document.class).get("job", Document.class).getString("job")) == null) {
                continue;
            }

            if (user.get("economy", Document.class).get("job", Document.class).getInteger("daysMissed") > getJob(user.get("economy", Document.class).get("job", Document.class).getString("business"), user.get("economy", Document.class).get("job", Document.class).getString("job")).getInteger("daysBeforeFire")) {
                Bson updates = Updates.combine(
                        Updates.set("economy.job.business", null),
                        Updates.set("economy.job.job", null),
                        Updates.set("economy.job.daysMissed", 0)
                );

                userData.updateOne(query, updates, new UpdateOptions().upsert(true));
                removeBytes(user.getString("userID"), getJob(user.get("economy", Document.class).get("job", Document.class).getString("business"), user.get("economy", Document.class).get("job", Document.class).getString("job")).getInteger("salary") * 5);
                System.out.println(user.getString("userID") + " was fired");
            }

            Bson updates = Updates.set("economy.job.worked", false);
            userData.updateOne(query, updates, new UpdateOptions().upsert(true));
        }
    }
    // endregion

    // Notifications
    // region
    public boolean getNotificationToggle(String userId) {
        try {
            return userData.find(new Document("userID", userId)).first().getBoolean("notifications");
        } catch (NullPointerException ne) {
            userData.updateOne(new Document("userID", userId), Updates.set("notifications", true), new UpdateOptions().upsert(true));
            return userData.find(new Document("userID", userId)).first().getBoolean("notifications");
        }
    }

    public void toggleNotifications(String userId) {
        Document query = new Document("userID", userId);
        UpdateOptions options = new UpdateOptions().upsert(true);

        try {
            Bson updates = Updates.set("notifications", !getNotificationToggle(userId));
            try {
                userData.updateOne(query, updates, options);
            } catch (MongoException me) {
                me.printStackTrace();
            }
        } catch (NullPointerException ne) {
            userData.updateOne(query, Updates.set("notifications", false), options);
        }
    }
    // endregion
}
