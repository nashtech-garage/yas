package com.yas.search.config;

import common.container.ContainerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.KafkaContainer;

@TestConfiguration
public class KafkaIntegrationTestConfiguration {

    @Value("${kafka.version}")
    private String kafkaVersion;

    @Value("${elasticsearch.version}")
    private String elasticSearchVersion;

    @Bean
    @ServiceConnection
    public KafkaContainer kafkaContainer(DynamicPropertyRegistry registry) {
        return ContainerFactory.kafkaContainer(registry, kafkaVersion);
    }

    @Bean
    @ServiceConnection
    public ElasticTestContainer elasticTestContainer() {
        return new ElasticTestContainer(elasticSearchVersion);
    }

}
