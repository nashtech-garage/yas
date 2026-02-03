package com.yas.recommendation.configuration;

import com.yas.recommendation.kafka.config.consumer.ProductCdcKafkaListenerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
@Import(ProductCdcKafkaListenerConfig.class)
public class KafkaConfiguration {
    @Value("${kafka.version}")
    private String kafkaVersion;

    @Value("${pgvector.version}")
    private String pgVectorVersion;

    @Bean
    @ServiceConnection
    public KafkaContainer kafkaContainer() {
        return new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:%s".formatted(kafkaVersion))
        );
    }

    @Bean()
    public DynamicPropertyRegistrar kafkaProperties(KafkaContainer kafkaContainer) {
        return registry -> {
            registry.add("spring.kafka.bootstrap-servers",
                kafkaContainer::getBootstrapServers);
            registry.add("spring.kafka.consumer.bootstrap-servers",
                kafkaContainer::getBootstrapServers);
            registry.add("spring.kafka.producer.bootstrap-servers",
                kafkaContainer::getBootstrapServers);
            registry.add("spring.kafka.consumer.auto-offset-reset",
                () -> "earliest");
        };
    }

    @Bean
    @ServiceConnection
    public PostgreSQLContainer pgvectorContainer() {
        var image = DockerImageName.parse("pgvector/pgvector:%s".formatted(pgVectorVersion))
            .asCompatibleSubstituteFor("postgres");
        return new PostgreSQLContainer<>(image);
    }

    @Bean
    public DynamicPropertyRegistrar pgvectorProperties(PostgreSQLContainer pgvectorContainer) {
        return registry -> {
            registry.add("spring.datasource.url", pgvectorContainer::getJdbcUrl);
            registry.add("spring.datasource.username", pgvectorContainer::getUsername);
            registry.add("spring.datasource.password", pgvectorContainer::getPassword);
        };
    }
}