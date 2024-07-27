package com.springai.controller;

import com.springai.model.QueryRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.Generation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@RestController
public class ConversationController {

    private final ChatClient chatClient;

    public ConversationController(ChatClient.Builder builder) {
        InMemoryChatMemory memory = new InMemoryChatMemory();
        this.chatClient = builder.
                defaultAdvisors(
                  new PromptChatMemoryAdvisor(memory),
                  new MessageChatMemoryAdvisor(memory)
                )
                .build();
    }

    @PostMapping("/chatMemory")
    public String basicChatWithSystemPrompt(@RequestBody QueryRequest request) {
        String chatId = "101";
        return this.chatClient.prompt()
                .user(request.getQuery())
                .advisors(a -> a
                                .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                                .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .call().content();
    }
}
