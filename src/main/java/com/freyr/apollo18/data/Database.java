package com.freyr.apollo18.data;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.data.codec.business.JobCodec;
import com.freyr.apollo18.data.codec.business.StockCodec;
import com.freyr.apollo18.data.codec.guild.GreetingCodec;
import com.freyr.apollo18.data.codec.guild.LevelingCodec;
import com.freyr.apollo18.data.codec.user.UserLevelingCodec;
import com.freyr.apollo18.data.codec.user.economy.UserCreditCardCodec;
import com.freyr.apollo18.data.codec.user.economy.UserJobCodec;
import com.freyr.apollo18.data.codec.user.economy.UserStockCodec;
import com.freyr.apollo18.data.codec.user.music.SongCodec;
import com.freyr.apollo18.data.provider.*;
import com.freyr.apollo18.data.records.Transaction;
import com.freyr.apollo18.data.records.business.Business;
import com.freyr.apollo18.data.records.business.Job;
import com.freyr.apollo18.data.records.business.Stock;
import com.freyr.apollo18.data.records.guild.Greeting;
import com.freyr.apollo18.data.records.guild.Guild;
import com.freyr.apollo18.data.records.guild.Leveling;
import com.freyr.apollo18.data.records.user.UserLeveling;
import com.freyr.apollo18.data.records.user.economy.*;
import com.freyr.apollo18.data.records.user.music.Playlist;
import com.freyr.apollo18.data.records.user.music.Song;
import com.freyr.apollo18.data.records.user.music.UserMusic;
import com.freyr.apollo18.handlers.BusinessHandler;
import com.freyr.apollo18.util.textFormatters.RandomString;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.lang.Nullable;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
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
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * This class handles everything that is connected to databases.
 * THIS IS THE ONLY CLASS THAT HAS DIRECT ACCESS TO THE DATABASE
 *
 * @author Freyr
 */
public class Database {

    private final Apollo18 bot;

    private final MongoCollection<Guild> guildData; // The collection of documents for guilds
    private final MongoCollection<com.freyr.apollo18.data.records.user.User> userData; // The collection of documents for users
    private final MongoCollection<Business> businessData;
    private final MongoCollection<Transaction> transactionData;

    /**
     * Creates a connection to the database and the collections
     *
     * @param srv The connection string
     */
    public Database(String srv, Apollo18 bot) {
        this.bot = bot;
        MongoClient mongoClient = new MongoClient(new MongoClientURI(srv));

        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(CodecRegistries.fromCodecs(new JobCodec(), new StockCodec(), new GreetingCodec(), new LevelingCodec(), new UserLevelingCodec(), new UserCreditCardCodec(), new UserJobCodec(), new UserStockCodec(), new SongCodec()), CodecRegistries.fromProviders(new BusinessCodecProvider(), new GuildCodecProvider(), new TransactionCodecProvider(), new UserCardCodecProvider(), new UserEconomyCodecProvider(), new PlaylistCodecProvider(), new UserCodecProvider()), MongoClientSettings.getDefaultCodecRegistry());

        MongoDatabase database = mongoClient.getDatabase("apollo").withCodecRegistry(codecRegistry);

        guildData = database.getCollection("guildData", Guild.class);
        userData = database.getCollection("userData", com.freyr.apollo18.data.records.user.User.class);
        businessData = database.getCollection("businesses", Business.class);
        transactionData = database.getCollection("transactions", Transaction.class);
    }

    public void createGuildData(net.dv8tion.jda.api.entities.Guild guild) {
        if (checkIfGuildExists(guild)) return;

        Leveling levelingData = new Leveling(true, null, "Congratulations [member], you have leveled up to [level]!");
        Greeting greetingData = new Greeting(false, null, null, null, "[member] has joined [server]!", "[member] has left [server].");

        guildData.insertOne(new Guild(guild.getId(), levelingData, greetingData));
    }

    public boolean createUserData(User user) {
        if (user.isBot()) return false;
        if (checkIfUserExists(user)) {
            return false;
        }

        List<UserLeveling> xp = new ArrayList<>();

        List<UserStock> stocks = new ArrayList<>();
        List<Playlist> playlists = new ArrayList<>();

        UserEconomy economyData = new UserEconomy(0, 0, new UserJob(null, null, 0, 0, false), new UserCard(false, new UserCreditCard(false, 0, 0, null)), stocks);
        UserMusic musicData = new UserMusic(playlists);

        try {
            userData.insertOne(new com.freyr.apollo18.data.records.user.User(user.getId(), xp, economyData, musicData, true));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public FindIterable<com.freyr.apollo18.data.records.user.User> getAllUsers() {
        return userData.find();
    }

    public FindIterable<Guild> getAllGuilds() {
        return guildData.find();
    }

    public com.freyr.apollo18.data.records.user.User getUser(String userId) {
        return userData.find(new Document("userID", userId)).first();
    }

    public Guild getGuild(String guildId) {
        return guildData.find(new Document("guildID", guildId)).first();
    }

    private boolean checkIfUserExists(User user) {
        FindIterable<com.freyr.apollo18.data.records.user.User> iterable = userData.find(new Document("userID", user.getId()));
        return iterable.first() != null;
    }

    private boolean checkIfGuildExists(net.dv8tion.jda.api.entities.Guild guild) {
        FindIterable<Guild> iterable = guildData.find(new Document("guildID", guild.getId()));
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
        return Objects.requireNonNull(guildData.find(new Document("guildID", guildId)).first()).greeting().onOff();
    }

    public String getWelcomeChannel(String guildId) {
        return Objects.requireNonNull(guildData.find(new Document("guildID", guildId)).first()).greeting().welcomeChannel();
    }

    public String getLeaveChannel(String guildId) {
        return Objects.requireNonNull(guildData.find(new Document("guildID", guildId)).first()).greeting().leaveChannel();
    }

    public String getWelcomeMessage(String guildId) {
        return Objects.requireNonNull(guildData.find(new Document("guildID", guildId)).first()).greeting().welcomeMessage();
    }

    public String getLeaveMessage(String guildId) {
        return Objects.requireNonNull(guildData.find(new Document("guildID", guildId)).first()).greeting().leaveMessage();
    }

    public String getMemberCountChannel(String guildId) {
        return Objects.requireNonNull(guildData.find(new Document("guildID", guildId)).first()).greeting().memberCountChannel();
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
        com.freyr.apollo18.data.records.user.User userDoc = userData.find(new Document("userID", userId)).first();

        assert userDoc != null;
        List<UserLeveling> xp = userDoc.leveling();

        for (UserLeveling doc : xp) {
            if (doc.guildID().equals(guildId)) {
                return true;
            }
        }

        return false;
    }

    public boolean getLevelingSystemToggle(String guildId) {
        return Objects.requireNonNull(guildData.find(new Document("guildID", guildId)).first()).leveling().onOff();
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
        return Objects.requireNonNull(guildData.find(new Document("guildID", guildId)).first()).leveling().channel();
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
        return Objects.requireNonNull(guildData.find(new Document("guildID", guildId)).first()).leveling().levelingMessage();
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

    public UserLeveling getUserLevelingProfile(String userId, String guildId) {
        com.freyr.apollo18.data.records.user.User userDoc = userData.find(new Document("userID", userId)).first();
        UserLeveling guildUserXpData = null;
        for (UserLeveling doc : userDoc.leveling()) {
            if (doc.guildID().equals(guildId)) {
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

    public FindIterable<com.freyr.apollo18.data.records.user.User> getLevelingLeaderboard(String guildId, int limit) {
        return userData.find(new Document("leveling.guildID", guildId)).sort(Sorts.descending("leveling.totalXp")).limit(limit);
    }
    // endregion

    // Economy System
    // region
    public UserEconomy getEconomyUser(String userId) {
        return Objects.requireNonNull(userData.find(new Document("userID", userId)).first()).economy();
    }

    public int getBalance(String userId) {
        return Objects.requireNonNull(userData.find(new Document("userID", userId)).first()).economy().balance();
    }

    public int getBank(String userId) {
        return Objects.requireNonNull(userData.find(new Document("userID", userId)).first()).economy().bank();
    }

    public int getNetWorth(String userID) {
        int balance = getBalance(userID);
        int bank = getBank(userID);
        int totalStockPrice = 0;

        List<Business> businesses = getBusinesses();

        for (Business business : businesses) {
            totalStockPrice += getTotalStocks(userID, business.stockCode()) * business.stock().currentPrice();
        }

        return balance + bank + totalStockPrice;
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

    public AggregateIterable<com.freyr.apollo18.data.records.user.User> getEconomyLeaderboard(String guildId, int limit) {
        return userData.aggregate(Arrays.asList(Aggregates.match(Filters.and(Filters.eq("leveling.guildID", guildId), Filters.eq("leveling.inServer", true))), Aggregates.addFields(new Field("sum", Filters.eq("$add", Arrays.asList("balance", "$bank")))), Aggregates.sort(Sorts.descending("sum"))));
    }
    // endregion

    // Transactions
    // region

    public void createTransaction(String userId, String transactionType, int oldBal, int newBal) {
        Transaction transaction = new Transaction(userId, (newBal - oldBal), oldBal, newBal, transactionType, DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm:ss").format(LocalDateTime.now()));

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

        List<Song> songs = getSongs(userId, playlist);
        if (songs == null) {
            throw new NullPointerException("Couldn't find the song");
        }

        Document data = new Document("songName", song.getInfo().title).append("uri", song.getInfo().uri).append("position", songs.size());

        Bson update = Updates.push("music.playlists.$[ele].songs", data);

        try {
            userData.updateOne(filter, update, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }
    }

    public void removeSong(String userId, String playlist, String songName) {
        if (checkIfSongExists(userId, playlist, songName)) {
            return;
        }

        Bson filter = Filters.and(Filters.eq("userID", userId));
        UpdateOptions options = new UpdateOptions().arrayFilters(List.of(Filters.eq("ele.playlistName", playlist)));

        Bson update = Updates.pull("music.playlists.$[ele].songs", new Document("songName", songName));

        try {
            userData.updateOne(filter, update, options);
        } catch (MongoException me) {
            me.printStackTrace();
        }

    }

    public void moveSong(String userId, String playlist, String songName, int newPos) {
        if (checkIfSongExists(userId, playlist, songName)) {
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

    public List<Playlist> getPlaylists(String userId) {
        return userData.find(new Document("userID", userId)).first().music().playlists();
    }

    public List<Song> getSongs(String userId, String playlist) {
        com.freyr.apollo18.data.records.user.User userDoc = userData.find(new Document("userID", userId)).first();
        Playlist userPlaylist = null;
        for (Playlist doc : userDoc.music().playlists()) {
            if (doc.playlistName().equals(playlist)) {
                userPlaylist = doc;
                break;
            }
        }

        if (userPlaylist == null) {
            return null;
        }

        return userPlaylist.songs();
    }

    private boolean checkIfSongExists(String userId, String playlist, String songName) {
        return userData.find(new Document("userID", userId).append("music.playlists.playlistName", playlist).append("music.playlists.songs.songName", songName)).first() == null;
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

            Stock stockData = new Stock(ticker, currentPrice, previousPrice, change, BusinessHandler.getArrow(change));

            businessData.insertOne(new Business(businessName, stockCode, "default", businessDescription, (logo == null) ? "https://library.kissclipart.com/20181224/fww/kissclipart-free-vector-building-clipart-computer-icons-66d576fc7c1dd7ff.png" : logo, true, new ArrayList<Job>(), stockData));
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public Business getBusiness(String stockCode) {
        return businessData.find(new Document("stockCode", stockCode)).first();
    }

    public List<Business> getBusinesses() {
        List<Business> result = new ArrayList<>();

        FindIterable<Business> businesses = businessData.find(new Document("public", true));
        for (Business business : businesses) {
            result.add(business);
        }

        return result;
    }

    public void addStockToUser(Business business, String userId, int quantity) {
        Document userBusiness = new Document("_id", new RandomString(12).nextString()).append("stockCode", business.stockCode()).append("purchasePrice", business.stock().currentPrice()).append("quantity", quantity);
        Document query = new Document("userID", userId);

        Bson updates = Updates.push("economy.stocks", userBusiness);

        UpdateOptions options = new UpdateOptions().upsert(true);

        int oldBal = getBalance(userId);

        userData.updateOne(query, updates, options);
        removeBytes(userId, business.stock().currentPrice() * quantity);

        createTransaction(userId, "Business / Stock / Buy", oldBal, getBalance(userId));
    }

    public void removeStockFromUser(String userId, String stockCode, int quantity) {
        List<Document> actions = getPurchasedStocks(userId, stockCode, quantity);
        Business business = getBusiness(stockCode);
        int currentPrice = business.stock().currentPrice();

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

        com.freyr.apollo18.data.records.user.User userDoc = userData.find(new Document("userID", userId)).first();
        List<UserStock> stocks = userDoc.economy().stocks();
        int tempQuantity = quantity;

        System.out.println("Info: Building Document for Stock: " + stockCode + ", Quantity: " + tempQuantity);

        for (int i = 0; i < stocks.size(); i++) {
            if (stocks.get(i).stockCode().equals(stockCode.toUpperCase()) && tempQuantity > 0) {
                if (stocks.get(i).quantity() <= tempQuantity && (tempQuantity - stocks.get(i).quantity()) >= 0) {
                    tempQuantity -= stocks.get(i).quantity();
                    System.out.println("Info: Action: Delete | SQ: " + stocks.get(i).quantity() + " | TQ Left: " + tempQuantity);
                    result.add(new Document("_id", stocks.get(i)._id()).append("quantity", stocks.get(i).quantity()).append("index", i).append("action", 0));
                } else if (stocks.get(i).quantity() > tempQuantity) {
                    System.out.println("Info: Action: Update | SQ from: " + stocks.get(i).quantity() + " to: " + (stocks.get(i).quantity() - tempQuantity));
                    result.add(new Document("_id", stocks.get(i)._id()).append("quantity", (stocks.get(i).quantity() - tempQuantity)).append("index", i).append("action", 1));
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
            List<UserStock> purchasedStocks = userData.find(new Document("userID", userId)).first().economy().stocks();
            int totalStocks = 0;
            for (UserStock purchasedStock : purchasedStocks) {
                if (purchasedStock.stockCode().equals(stockCode)) {
                    totalStocks += purchasedStock.quantity();
                }
            }

            return totalStocks;
        } catch (Exception e) {
            return 0;
        }
    }

    public void updateStocks() {
        FindIterable<Business> businesses = businessData.find();

        for (Business business : businesses) {
            try {
                Document query = new Document("stockCode", business.stockCode());
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://realstonks.p.rapidapi.com/" + business.stock().ticker())).header("X-RapidAPI-Key", bot.getConfig().get("RAPIDAPI_KEY", System.getenv("RAPIDAPI_KEY"))).header("X-RapidAPI-Host", "realstonks.p.rapidapi.com").method("GET", HttpRequest.BodyPublishers.noBody()).build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                JSONObject data = new JSONObject(response.body());

                int currentPrice = (int) Math.round(data.getDouble("price") * 0.23);
                int previousPrice = business.stock().currentPrice();
                int change = currentPrice - previousPrice;

                Bson updates = Updates.combine(Updates.set("stock.currentPrice", currentPrice), Updates.set("stock.previousPrice", previousPrice), Updates.set("stock.change", change), Updates.set("stock.arrowEmoji", BusinessHandler.getArrow(change)));

                businessData.updateOne(query, updates, new UpdateOptions().upsert(true));
                createTransaction("stockUpdate", "Business / Stock / Update", previousPrice, currentPrice);

                StockData stockData = new StockData(bot, business.stock().ticker());
                stockData.displayLineChart(stockData.parseStockData(stockData.retrieveStockData()));

                System.out.println("Updating " + business.stock().ticker() + "; Old Price: " + previousPrice + "; Current Price: " + currentPrice + "; Change: " + change + "\nAll Details: " + data);

                TimeUnit.SECONDS.sleep(5);
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

    public List<Job> getJobs(String code) {
        return businessData.find(new Document("stockCode", code)).first().jobs();
    }

    public Job getJob(String code, String jobName) {
        Business business = getBusiness(code);
        if (business == null) return null;
        Job job = null;
        for (Job currentJob : business.jobs()) {
            if (currentJob.name().equalsIgnoreCase(jobName)) {
                job = currentJob;
                break;
            }
        }

        return job;
    }

    public boolean work(String userId) {
        UserEconomy userEconomyDoc = userData.find(new Document("userID", userId)).first().economy();
        Job userJob = getJob(userEconomyDoc.job().businessCode(), userEconomyDoc.job().jobName());
        if (userJob == null) return false;

        addBytes(userId, userJob.salary());
        Document query = new Document("userID", userId);

        if (Integer.valueOf(userEconomyDoc.job().daysMissed()) == null) {
            UserJob job = new UserJob(userEconomyDoc.job().businessCode(), userEconomyDoc.job().jobName(), 1, 0, true);

            Bson updates = Updates.set("economy.job", job);

            userData.updateOne(new Document("userID", userId), updates, new UpdateOptions().upsert(true));
            return true;
        }

        if (!userEconomyDoc.job().worked()) {
            Bson updates = Updates.combine(Updates.inc("economy.job.daysWorked", 1), Updates.set("economy.job.worked", true), Updates.set("economy.job.daysMissed", 0));

            userData.updateOne(query, updates, new UpdateOptions().upsert(true));
            createTransaction(userId, "Job / Work", getBalance(userId) - userJob.salary(), getBalance(userId));
        }
        return true;
    }

    public void setJob(String userId, String code, String jobName) {
        Document query = new Document("userID", userId);
        Job job = getJob(code, jobName);

        Bson updates = Updates.combine(Updates.set("economy.job.business", code), Updates.set("economy.job.job", job.name()));

        userData.updateOne(query, updates, new UpdateOptions().upsert(true));
    }

    public UserJob getUserJob(String userId) {
        return userData.find(new Document("userID", userId)).first().economy().job();
    }

    // endregion

    public void dailyWorkChecks() {
        for (com.freyr.apollo18.data.records.user.User user : userData.find(new Document())) {
            Document query = new Document("userID", user.userID());
            System.out.println("Updating data for " + user.userID());
            if (Integer.valueOf(user.economy().job().daysMissed()) == null || Boolean.valueOf(user.economy().job().worked()) == null) {
                Document job = new Document("business", null).append("job", null).append("daysWorked", 0).append("daysMissed", 0).append("worked", false);
                Bson updates = Updates.set("economy.job", job);

                userData.updateOne(query, updates, new UpdateOptions().upsert(true));
                System.out.println("Replaced Job Data with Updated Job Data for " + user.userID());
            }

            if (!user.economy().job().worked() && user.economy().job().jobName() != null) {
                Bson updates = Updates.combine(Updates.set("economy.job.daysWorked", 0), Updates.inc("economy.job.daysMissed", 1));

                userData.updateOne(query, updates, new UpdateOptions().upsert(true));
                System.out.println(user.userID() + " did not work today. Days missed added");
            }

            if (getJob(user.economy().job().businessCode(), user.economy().job().jobName()) == null) {
                continue;
            }

            if (user.economy().job().daysMissed() > getJob(user.economy().job().businessCode(), user.economy().job().jobName()).daysBeforeFire()) {
                Bson updates = Updates.combine(Updates.set("economy.job.business", null), Updates.set("economy.job.job", null), Updates.set("economy.job.daysMissed", 0));

                userData.updateOne(query, updates, new UpdateOptions().upsert(true));
                removeBytes(user.userID(), getJob(user.economy().job().businessCode(), user.economy().job().jobName()).salary() * 5);
                System.out.println(user.userID() + " was fired");
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
            return Objects.requireNonNull(userData.find(new Document("userID", userId)).first()).notifications();
        } catch (NullPointerException ne) {
            userData.updateOne(new Document("userID", userId), Updates.set("notifications", true), new UpdateOptions().upsert(true));
            return Objects.requireNonNull(userData.find(new Document("userID", userId)).first()).notifications();
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
