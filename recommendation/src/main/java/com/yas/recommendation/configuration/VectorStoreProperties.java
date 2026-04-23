package com.yas.recommendation.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.ai.vectorstore.pgvector")
public class VectorStoreProperties {
    private int dimensions;
    private PgVectorStore.PgDistanceType distanceType;
    private PgVectorStore.PgIndexType indexType;
    private boolean initializeSchema;
}