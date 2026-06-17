package com.yas.commonlibrary;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;

@TestConfiguration
@ConditionalOnProperty(name = "it.enabled", havingValue = "true", matchIfMissing = false)
public class IntegrationTestConfiguration {

    private static final org.testcontainers.containers.Network SHARED_NETWORK = org.testcontainers.containers.Network.newNetwork();


    @Bean(destroyMethod = "stop")
    @ServiceConnection
    public PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")
            .withNetwork(SHARED_NETWORK)
            .withReuse(false) // TẮT REUSE ĐỂ TRÁNH RÁC TRÊN JENKINS
            .waitingFor(Wait.forListeningPort())
            .withStartupTimeout(Duration.ofMinutes(2));
    }

    @Bean(destroyMethod = "stop")
    public KeycloakContainer keycloakContainer() {
        return new KeycloakContainer("quay.io/keycloak/keycloak:26.0")
            .withNetwork(SHARED_NETWORK)
            .withRealmImportFiles("/test-realm.json")
            .withEnv("KC_HTTP_ENABLED", "true")
            .withEnv("KC_HOSTNAME_STRICT", "false")
            .withEnv("KC_BOOTSTRAP_ADMIN_USERNAME", "admin")
            .withEnv("KC_BOOTSTRAP_ADMIN_PASSWORD", "admin")
            .withEnv("KC_HEALTH_ENABLED", "true") // Kích hoạt endpoint health check
            .withReuse(false) // TẮT REUSE
            .waitingFor(Wait.forListeningPort())
            .withStartupTimeout(Duration.ofMinutes(5))
            .withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withMemory(1024L * 1024 * 1024));
    }

    @Bean
    public DynamicPropertyRegistrar integrationTestProperties(
            PostgreSQLContainer<?> postgresContainer, 
            KeycloakContainer keycloakContainer) {
        return registry -> {
            // DATABASE PROPERTIES - CHỐT HẠ LỖI DIALECT
            registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
            registry.add("spring.datasource.username", postgresContainer::getUsername);
            registry.add("spring.datasource.password", postgresContainer::getPassword);
            registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
            registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
            registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");

            // KEYCLOAK PROPERTIES
            registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloakContainer.getAuthServerUrl() + "/realms/quarkus");
            registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                () -> keycloakContainer.getAuthServerUrl() + "/realms/quarkus/protocol/openid-connect/certs");
        };
    }
}