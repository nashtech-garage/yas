package com.yas.customer.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.keycloak.OAuth2Constants.CLIENT_CREDENTIALS;
import static org.keycloak.OAuth2Constants.PASSWORD;

@Configuration
public class KeycloakClientConfig {
    static Keycloak keycloak = null;
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

    @Bean
    public Keycloak getAdminKeyCloak(){
        if(keycloak == null) {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(keycloakPropsConfig.getAuthServerUrl())
                    .realm(keycloakPropsConfig.getRealm())
                    .grantType(PASSWORD)
                    .username("admin@yas.com")
                    .password("admin")
                    .clientId(keycloakPropsConfig.getResource())
                    .clientSecret(keycloakPropsConfig.getCredentials().getSecret())
                    .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                    .build();
        }
        return keycloak;
    }
}
