package com.yas.commonlibrary;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
public class IntegrationTestConfiguration {

    @Bean(destroyMethod = "stop")
    @ServiceConnection
    public PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:16")
            .withReuse(true);
    }

    @Bean(destroyMethod = "stop")
    public KeycloakContainer keycloakContainer() {
        return new KeycloakContainer()
            .withRealmImportFiles("/test-realm.json")
            .withReuse(true);
    }

    @Bean
    public DynamicPropertyRegistrar keycloakDynamicProperties(KeycloakContainer keycloakContainer) {
        return registry -> {
            registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloakContainer.getAuthServerUrl() + "/realms/quarkus"
            );
            registry.add(
                "spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                () -> keycloakContainer.getAuthServerUrl()
                    + "/realms/quarkus/protocol/openid-connect/certs"
            );
        };
    }
}