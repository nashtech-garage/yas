package com.yas.recommendation.chat;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.RelevancyEvaluator;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

@Component
public class ChatService {

//    private final ChatMemory chatMemory;
//    private final RelevancyEvaluator relevancyEvaluator;
//    private final ChatModel chatModel;
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public ChatService(ChatClient chatClient, VectorStore vectorStore) {
//        this.chatMemory = chatMemory;
//        this.relevancyEvaluator = relevancyEvaluator;
//        this.chatModel = chatModel;
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }


    public String chat(String message) {
        var systemPromptTemplate = """
                You are a helpful assistant, conversing with a user about the subjects contained in a set of documents.
                Use the information from the DOCUMENTS section to provide accurate answers. If unsure or if the answer
                isn't found in the DOCUMENTS section, simply state that you don't know the answer and do not mention
                the DOCUMENTS section.
                
                DOCUMENTS:
                {documents}
                """;

        List<Document> similarDocuments = vectorStore.similaritySearch(SearchRequest.query(message).withTopK(5));
        String content = similarDocuments.stream().map(Document::getContent).collect(Collectors.joining(System.lineSeparator()));

        return chatClient.prompt()
            .system(systemSpec -> systemSpec
                .text(systemPromptTemplate)
                .param("documents", content)
            )
            .user(message)
            .call()
            .content();
    }
}
