package com.springai.controller;

import com.springai.model.QueryRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
public class BasicController {
    private final ChatClient chatClient;

    public BasicController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @PostMapping("/basicChat")
    public List<Generation> basicChat(@RequestBody QueryRequest request) {
        PromptTemplate promptTemplate = new PromptTemplate(request.getQuery());
        Prompt prompt = promptTemplate.create();
        ChatClient.ChatClientRequest.CallPromptResponseSpec res = chatClient.prompt(prompt).call();

        return res.chatResponse().getResults();
    }

    @PostMapping("/chatWithSystemPrompt")
    public List<Generation> basicChatWithSystemPrompt(@RequestBody QueryRequest request) {
        Message userMessage = new UserMessage(request.getQuery());
        Message systemMessage = new SystemMessage("You are an expert in Java. Please provide to the point and accurate answers.");
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        ChatClient.ChatClientRequest.CallPromptResponseSpec res = chatClient.prompt(prompt).call();

        return res.chatResponse().getResults();
    }

    @PostMapping("/chatWithStreamingResponse")
    public Flux<ChatResponse> chatWithStreamingResponse(@RequestBody QueryRequest request) {

        Message userMessage = new UserMessage(request.getQuery());
        Message systemMessage = new SystemMessage("You are an expert in Java. Please provide to the point and accurate answers.");
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        Flux<ChatResponse> flux = chatClient.prompt(prompt).stream().chatResponse();

        flux.subscribe(
          chatResponse -> System.out.print(chatResponse.getResult().getOutput().getContent()), // onNext
          throwable -> System.err.println("Error occurred: " + throwable), // onError
                () -> System.out.println("Streaming complete") // onComplete
        );

        return flux;
    }



}
