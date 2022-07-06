package com.freyr.apollo18.commands.utility;

import com.freyr.apollo18.Apollo18;
import com.freyr.apollo18.commands.Category;
import com.freyr.apollo18.commands.Command;
import com.freyr.apollo18.util.embeds.EmbedColor;
import com.freyr.apollo18.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * This command uses the top.gg api to help you vote for Apollo
 *
 * @author Freyr
 */
public class VoteCommand extends Command {

    public VoteCommand(Apollo18 bot) {
        super(bot);
        this.name = "vote";
        this.description = "Vote for Apollo on Top.gg!";
        this.category = Category.UTILITY;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://top.gg/api/bots/" + "853812538218381352" + "/check?userId=" + event.getUser().getId())).header("Authorization", "Bearer " + bot.getConfig().get("TOPGG_TOKEN")).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject data = new JSONObject(response.body()); // Grabbing data from api

            // Checking if user has voted
            if (data.getInt("voted") == 0) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Vote for Apollo18!");
                embed.setThumbnail("https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/53653317-d541-48a0-8252-bc533c980e00/ddk20gg-5d876f13-a2e3-4a64-a589-45e1cee6ee40.png/v1/fill/w_600,h_835,strp/apollo___greek_mythology_by_yliade_ddk20gg-fullview.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7InBhdGgiOiJcL2ZcLzUzNjUzMzE3LWQ1NDEtNDhhMC04MjUyLWJjNTMzYzk4MGUwMFwvZGRrMjBnZy01ZDg3NmYxMy1hMmUzLTRhNjQtYTU4OS00NWUxY2VlNmVlNDAucG5nIiwiaGVpZ2h0IjoiPD04MzUiLCJ3aWR0aCI6Ijw9NjAwIn1dXSwiYXVkIjpbInVybjpzZXJ2aWNlOmltYWdlLndhdGVybWFyayJdLCJ3bWsiOnsicGF0aCI6Ilwvd21cLzUzNjUzMzE3LWQ1NDEtNDhhMC04MjUyLWJjNTMzYzk4MGUwMFwveWxpYWRlLTQucG5nIiwib3BhY2l0eSI6OTUsInByb3BvcnRpb25zIjowLjQ1LCJncmF2aXR5IjoiY2VudGVyIn19.kJ-P4pwq5NVxQWBqvl_x7s4OQqKyp5GB7202NMCuxP4");
                embed.setDescription("Click [here](https://top.gg/bot/853812538218381352/vote) to vote for me!\nYou can vote every 12 hours.");
                embed.setColor(EmbedColor.DEFAULT_COLOR);

                event.getHook().sendMessageEmbeds(embed.build()).addActionRow(Button.link("https://top.gg/bot/853812538218381352/vote", "Vote")).queue();
            } else event.getHook().sendMessageEmbeds(EmbedUtils.createError("You have already voted! You can vote every 12 hours.")).queue();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            event.getHook().sendMessageEmbeds(EmbedUtils.createError("Something went wrong while getting the data. Please try again later.")).queue();
        }
    }
}
