package common.container;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Factory class that holds and provides containers used for testing with Testcontainers.
 */
public final class ContainerFactory {

    private ContainerFactory() {}

    public static KeycloakContainer keycloakContainer() {
        return new KeycloakContainer()
            .withRealmImportFiles("/test-realm.json")
            .withReuse(true);
    }

    public static KafkaContainer kafkaContainer(String version) {
        return new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:%s".formatted(version))
        );
    }

    public static PostgreSQLContainer pgvector(String version) {
        var image = DockerImageName.parse("pgvector/pgvector:%s".formatted(version))
            .asCompatibleSubstituteFor("postgres");
        return new PostgreSQLContainer<>(image);
    }

}
