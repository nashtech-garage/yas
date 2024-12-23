package com.yas.recommendation.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "yas.recommendation.embedding-based.search")
public record EmbeddingSearchConfiguration(Double similarityThreshold, int topK) {}
