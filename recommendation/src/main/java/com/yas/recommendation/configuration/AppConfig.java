package com.yas.recommendation.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    TokenTextSplitter tokenTextSplitter() {
        return new TokenTextSplitter();
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
