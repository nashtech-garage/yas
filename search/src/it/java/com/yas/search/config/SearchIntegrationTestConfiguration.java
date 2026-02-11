package com.yas.search.config;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class SearchIntegrationTestConfiguration {

    @Value("${kafka.version}")
    private String kafkaVersion;

    @Value("${elasticsearch.version}")
    private String elasticSearchVersion;

    @Bean
    public KafkaContainer kafkaContainer() {
        return new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:%s".formatted(kafkaVersion)));
    }

    @Bean
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
    public ElasticTestContainer elasticTestContainer() {
        return new ElasticTestContainer(elasticSearchVersion);
    }

    @Bean(destroyMethod = "stop")
    public KeycloakContainer keycloakContainer() {
        return new KeycloakContainer()
            .withRealmImportFiles("/test-realm.json")
            .withReuse(true);
    }

    @Bean
    public DynamicPropertyRegistrar keycloakDynamicProperties() {
        return registry -> {
            registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloakContainer().getAuthServerUrl() + "/realms/quarkus"
            );
            registry.add(
                "spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                () -> keycloakContainer().getAuthServerUrl()
                    + "/realms/quarkus/protocol/openid-connect/certs"
            );
        };
    }

}