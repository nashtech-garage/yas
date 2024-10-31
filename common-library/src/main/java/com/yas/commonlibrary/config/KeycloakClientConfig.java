package com.yas.commonlibrary.config;

import static org.keycloak.OAuth2Constants.CLIENT_CREDENTIALS;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakClientConfig {
    private static final String AUTH_SERVER_URL = "http://identity";
    private static final String REALM = "Yas";
    private static final String RESOURCE = "service-to-service";
    private static final String CLIENT_SECRET = "6A1IpjtpvPYWzdLgi8j2zzVMlkjSGtBa";

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
            .grantType(CLIENT_CREDENTIALS)
            .serverUrl(AUTH_SERVER_URL)
            .realm(REALM)
            .clientId(RESOURCE)
            .clientSecret(CLIENT_SECRET)
            .build();
    }
}
