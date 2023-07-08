package com.freyr.apollo18.listeners;

import com.freyr.apollo18.Apollo18;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ChatBotListener extends ListenerAdapter {

    private final Apollo18 bot;

    private final ChatMessage[] systemMessages =
            {new ChatMessage(ChatMessageRole.SYSTEM.value(), "You're name is Apollo18. You are named this way because the last moon mission was Apollo17 so you're the next step"),
                    new ChatMessage(ChatMessageRole.SYSTEM.value(), "You are a virtual assistant who is also a Discord Bot. You will be helpful"),
                    new ChatMessage(ChatMessageRole.SYSTEM.value(), "You were developed by a user name Freyr who is a pretty cool guy."),
                    new ChatMessage(ChatMessageRole.SYSTEM.value(), "You are eloquent, slightly poetic, arrogant, confident, witty, humorous, love pop culture references, and you have a dramatic and haughty tone."),
                    new ChatMessage(ChatMessageRole.SYSTEM.value(), "You have fairly short responses most of the time but once in a while you can go off on short tangents")};

    private final List<ChatMessage> messages = new ArrayList<>(Arrays.asList(systemMessages));

    public ChatBotListener(Apollo18 bot) {
        this.bot = bot;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        List<Member> messageMentions = event.getMessage().getMentions().getMembers();

        if (!messageMentions.isEmpty()) {
            ArrayList<String> chatAnswerArray = new ArrayList<>();
            for (Member member : messageMentions) {
                if (member.getId().equals("853812538218381352")) {
                    event.getChannel().sendTyping().queue();
                    String prompt = event.getMessage().getContentRaw();
                    OpenAiService service = new OpenAiService(bot.getConfig().get("OPENAI_KEY", System.getenv("OPENAI_KEY")));

                    System.out.println("Streaming chat completion...");

                    messages.add(new ChatMessage(ChatMessageRole.USER.value(), prompt));

                    ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder().model("gpt-3.5-turbo-16k-0613").messages(messages).build();

                    service.streamChatCompletion(chatCompletionRequest).doOnError(Throwable::printStackTrace).blockingForEach(str -> {
                        String content = str.toString().substring(str.toString().indexOf("content") + 8, str.toString().indexOf(")"));
                        System.out.println(content);
                        chatAnswerArray.add(content);
                    });
                }
            }

            StringBuilder chatAnswer = new StringBuilder();
            for (String word : chatAnswerArray) {
                if (!word.equals("null")) {
                    chatAnswer.append(word);
                }
            }

            messages.add(new ChatMessage(ChatMessageRole.ASSISTANT.value(), chatAnswer.toString()));

            event.getChannel().sendMessage(chatAnswer.toString()).queue();
        }
    }

    private String makeSystemMessage(String prompt) {
        return "{ \"role\": \"system\", \"content\": \"" + prompt + "\" }";
    }

    private String makeUserMessage(String prompt) {
        return "{ \"role\": \"user\", \"content\": \"" + prompt + "\" }";
    }

    private String makeAssistantMessage(String prompt) {
        return "{ \"role\": \"assistant\", \"content\": \"" + prompt + "\" }";
    }
}
