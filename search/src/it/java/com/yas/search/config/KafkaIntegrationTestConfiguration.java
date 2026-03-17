package com.yas.search.config;

import common.container.ContainerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.KafkaContainer;

@TestConfiguration
public class KafkaIntegrationTestConfiguration {

    @Value("${kafka.version}")
    private String kafkaVersion;

    @Value("${elasticsearch.version}")
    private String elasticSearchVersion;

    @Bean
    @ServiceConnection
    public KafkaContainer kafkaContainer() {
        return ContainerFactory.kafkaContainer(kafkaVersion);
    }

    @Bean
    @ServiceConnection
    public ElasticTestContainer elasticTestContainer() {
        return new ElasticTestContainer(elasticSearchVersion);
    }

    @Bean
    public DynamicPropertyRegistrar elasticProperties(ElasticTestContainer elastic, KafkaContainer kafka) {
        return registry -> {
            registry.add("elasticsearch.url", elastic::getHttpHostAddress);
            registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        };
    }

}
