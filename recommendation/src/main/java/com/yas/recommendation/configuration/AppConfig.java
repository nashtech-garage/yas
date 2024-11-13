package com.yas.recommendation.configuration;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AppConfig {

    @Bean
    @Primary
    public ChatModel azureChatModel(AzureOpenAiChatModel azureOpenAiChatModel) {
        return azureOpenAiChatModel;
    }

    @Bean
    @Primary
    public EmbeddingModel embeddingModelModel(OllamaEmbeddingModel ollamaEmbeddingModel) {
        return ollamaEmbeddingModel;
    }

    @Bean
    public ObjectMapper objectMapper() {
        var o = new ObjectMapper();
        o.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        o.registerModule(new JavaTimeModule());
        return o;
    }

}
