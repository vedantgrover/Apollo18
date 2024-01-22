package com.freyr.apollo18.commands;

import com.freyr.apollo18.Apollo18;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import okhttp3.*;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a basic command layout. It allows us to create commands very easily. The command manager then can use
 * this data to add commands into Discord!
 *
 * @author Freyr
 */
public abstract class Command {

    public Apollo18 bot; // This gives us access to the config file which is in the main class
    public String name; // Name of the command
    public String description; // Description of the command
    public Category category; // The category it should be in within the help command
    public boolean devOnly = false; // Will restrict a command so that it can only be run by the developer
    public List<OptionData> args; // Any options the command needs goes here
    public List<SubcommandData> subCommands; // Any subcommands that the command has
    public Permission permission; // Permissions for the user
    public Permission botPermission; // Permissions the bot needs
    public int cooldown = 0;
    public int uses = 1;

    /**
     * Command Constructor.
     * Initializes args as an empty arraylist
     */
    public Command(Apollo18 bot) {
        this.bot = bot;
        this.args = new ArrayList<>();
        this.subCommands = new ArrayList<>();
    }

    /**
     * This method will contain the code that we want the bot to execute when that command is called.
     *
     * @param event Has all the information about the event.
     */
    public abstract void execute(SlashCommandInteractionEvent event);

    /**
     * This command uses an API URL to grab data and return it for use
     *
     * @param apiURL The api url you want to use
     * @return All the data given through that URL
     */
    public JSONObject getApiData(String apiURL) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(apiURL).build();

        try {
            Response response = client.newCall(request).execute();

            String responseBody = response.body().string();

            response.close();
            return new JSONObject(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This command uses an API URL to grab data and return it for use
     *
     * @param apiURL The api url you want to use
     * @return All the data given through that URL
     */
    public JSONArray getApiDataArray(String apiURL) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(apiURL).build();

        try {
            Response response = client.newCall(request).execute();

            String responseBody = response.body().string();

            response.close();
            return new JSONArray(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This command uses an API URL to grab data and return it for use
     *
     * @param apiURL            The api url you want to use
     * @param apiAuthentication The authentication key (Bearer + [key])
     * @return All the data given through that URL
     */
    public JSONObject getApiData(String apiURL, String apiAuthentication) {

        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder().url(apiURL).addHeader("Authorization", "Bearer " + apiAuthentication).build();

        return getJsonObject(okHttpClient, request);
    }

    /**
     * Sends a post request to an API without authorization
     *
     * @param apiUrl The URL endpoint of the API
     * @param json   The request body
     * @return A JSONObject with the response body
     */
    public JSONObject postApiData(String apiUrl, String json) {
        OkHttpClient okHttpClient = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");

        RequestBody body = RequestBody.create(json, mediaType);

        Request request = new Request.Builder().url(apiUrl).post(body).build();

        return getJsonObject(okHttpClient, request);
    }

    @Nullable
    private JSONObject getJsonObject(OkHttpClient okHttpClient, Request request) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            System.out.println(responseBody);

            return new JSONObject(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
