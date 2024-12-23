package com.yas.recommendation.config;

import common.container.ContainerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class KafkaIntegrationTestConfiguration {

    @Value("${kafka.version}")
    private String kafkaVersion;

    @Value("${pgvector.version}")
    private String pgVectorVersion;

    @Bean
    @ServiceConnection
    public KafkaContainer kafkaContainer(DynamicPropertyRegistry registry) {
        return ContainerFactory.kafkaContainer(registry, kafkaVersion);
    }

    @Bean
    @ServiceConnection
    public PostgreSQLContainer pgvectorContainer(DynamicPropertyRegistry registry) {
        return ContainerFactory.pgvector(registry, pgVectorVersion);
    }
}
