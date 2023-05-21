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
import java.util.HashMap;
import java.util.List;

public class ChatBotListener extends ListenerAdapter {

    private final Apollo18 bot;

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
                    final List<ChatMessage> messages = new ArrayList<>();
                    final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), prompt);
                    messages.add(systemMessage);
                    ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder().model("gpt-3.5-turbo").messages(messages).n(1).maxTokens(3900).logitBias(new HashMap<>()).build();

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

            event.getChannel().sendMessage(chatAnswer.toString()).queue();
        }
    }
}
