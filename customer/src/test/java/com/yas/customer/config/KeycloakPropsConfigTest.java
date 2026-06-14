package com.yas.customer.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class KeycloakPropsConfigTest {

    @Test
    void testGettersAndSetters() {
        KeycloakPropsConfig config = new KeycloakPropsConfig();
        config.setAuthServerUrl("http://localhost:8080/auth");
        config.setRealm("yas");
        config.setResource("yas-client");

        KeycloakPropsConfig.Credentials credentials = config.new Credentials();
        credentials.setSecret("my-secret");
        config.setCredentials(credentials);

        assertEquals("http://localhost:8080/auth", config.getAuthServerUrl());
        assertEquals("yas", config.getRealm());
        assertEquals("yas-client", config.getResource());
        assertNotNull(config.getCredentials());
        assertEquals("my-secret", config.getCredentials().getSecret());
    }
}
