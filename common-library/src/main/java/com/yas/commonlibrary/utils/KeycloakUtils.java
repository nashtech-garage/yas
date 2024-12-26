package com.yas.commonlibrary.utils;

import jakarta.annotation.PostConstruct;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KeycloakUtils {

    private static Keycloak keycloakClient;

    @Autowired
    private Keycloak keycloak;

    @PostConstruct
    public void init() {
        KeycloakUtils.keycloakClient = keycloak;
    }

    public static String getAccessToken() {
        if (keycloakClient == null) {
            throw new IllegalStateException("Keycloak client not initialized");
        }
        return keycloakClient.tokenManager().getAccessTokenString();
    }
}
