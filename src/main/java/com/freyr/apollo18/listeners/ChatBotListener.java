package com.freyr.apollo18.listeners;

import com.freyr.apollo18.Apollo18;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatBotListener extends ListenerAdapter {

    private final Apollo18 bot;

    public ChatBotListener(Apollo18 bot) {
        this.bot = bot;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        List<Member> message = event.getMessage().getMentions().getMembers();

        for (Member member: message) {
            if (member.getId().equals("853812538218381352")) {
                OpenAiService service = new OpenAiService(bot.getConfig().get("OPENAI_TOKEN", System.getenv("OPENAI_TOKEN")));
                CompletionRequest completionRequest = CompletionRequest.builder()
                        .prompt("Somebody once told me the world is gonna roll me")
                        .model("gpt-3.5-turbo")
                        .echo(true)
                        .build();
                service.createCompletion(completionRequest).getChoices().forEach(System.out::println);
            }
        }
    }
}
