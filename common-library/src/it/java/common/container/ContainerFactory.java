package common.container;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Factory class that holds and provides containers used for testing with Testcontainers.
 */
public final class ContainerFactory {

    private ContainerFactory() {}

    public static KeycloakContainer keycloakContainer(DynamicPropertyRegistry registry) {
        KeycloakContainer keycloak = new KeycloakContainer()
            .withRealmImportFiles("/test-realm.json")
            .withReuse(true);

        registry.add(
            "spring.security.oauth2.resourceserver.jwt.issuer-uri",
            () -> "%s%s".formatted(keycloak.getAuthServerUrl(), "/realms/quarkus")
        );
        registry.add(
            "spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
            () -> "%s%s".formatted(keycloak.getAuthServerUrl(), "/realms/quarkus/protocol/openid-connect/certs")
        );
        return keycloak;
    }

    public static KafkaContainer kafkaContainer(DynamicPropertyRegistry registry, String version) {
        var kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:%s".formatted(version))
        );
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);

        // Consumer properties
        registry.add("auto.offset.reset", () -> "earliest");
        registry.add("spring.kafka.producer.bootstrap-servers", kafkaContainer::getBootstrapServers);

        // Producer properties
        registry.add("spring.kafka.consumer.bootstrap-servers", kafkaContainer::getBootstrapServers);
        return kafkaContainer;
    }

}
