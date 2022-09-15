package com.yas.customer.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.keycloak.OAuth2Constants.CLIENT_CREDENTIALS;

@Configuration
public class KeycloakClientConfig {

    private final KeycloakPropsConfig keycloakPropsConfig;

    public KeycloakClientConfig(KeycloakPropsConfig keycloakPropsConfig) {
        this.keycloakPropsConfig = keycloakPropsConfig;
    }

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .grantType(CLIENT_CREDENTIALS)
                .serverUrl(keycloakPropsConfig.getAuthServerUrl())
                .realm(keycloakPropsConfig.getRealm())
                .clientId(keycloakPropsConfig.getResource())
                .clientSecret(keycloakPropsConfig.getCredentials().getSecret())
                .build();
    }
}
