package com.yas.recommendation.chat;

import com.yas.recommendation.chat.util.ChatUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.stereotype.Component;

@Component
public class ChatService {

    private final ChatClient chatClient;
    private final PromptFactory promptFactory;

    public ChatService(ChatClient.Builder modelBuilder, PromptFactory promptFactory) {
        this.promptFactory = promptFactory;
        this.chatClient = modelBuilder
            .defaultAdvisors(
//                new PromptChatMemoryAdvisor(new InMemoryChatMemory())
            ).build();
    }

    public String chat(String message) {
        var user = ChatUtil.getUser();
        return chatClient.prompt()
//            .system(s ->
//                s.text(promptFactory.getSystemPrompt())
//                    .param("isAuthenticated", user.isAuthenticated())
//                    .param("userName", user.name())
//                    .param("termOfService", "termOfService")
//            )
            .user(message)
            .call()
            .content();
    }
}
