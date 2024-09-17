package com.yas.webhook.config;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class IntegrationTestConfiguration implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @ServiceConnection
    @Bean(destroyMethod = "stop")
    public PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("integration-tests-db")
            .withReuse(true);
    }

    @Bean(destroyMethod = "stop")
    public KeycloakContainer keycloakContainer(DynamicPropertyRegistry registry) {
        KeycloakContainer keycloak = new KeycloakContainer()
            .withRealmImportFiles("/test-realm.json")
            .withReuse(true);

        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
            () -> keycloak.getAuthServerUrl() + "/realms/quarkus");
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
            () -> keycloak.getAuthServerUrl() + "/realms/quarkus/protocol/openid-connect/certs");
        return keycloak;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.2.2.arm64"));
        kafka.start();
        setProperties(environment, "spring.kafka.bootstrap-servers", kafka.getBootstrapServers());
    }

    private void setProperties(ConfigurableEnvironment environment, String name, Object value) {
        MutablePropertySources sources = environment.getPropertySources();
        PropertySource<?> source = sources.get(name);
        if (source == null) {
            source = new MapPropertySource(name, new HashMap<>());
            sources.addFirst(source);
        }
        ((Map<String, Object>) source.getSource()).put(name, value);
    }
}
