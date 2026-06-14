package com.yas.customer.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;

class KeycloakClientConfigTest {

    @Test
    void keycloak_ShouldReturnKeycloakInstance() {
        KeycloakPropsConfig propsConfig = mock(KeycloakPropsConfig.class);
        when(propsConfig.getAuthServerUrl()).thenReturn("http://localhost:8080/auth");
        when(propsConfig.getRealm()).thenReturn("yas");
        when(propsConfig.getResource()).thenReturn("yas-client");
        
        KeycloakPropsConfig.Credentials credentials = mock(KeycloakPropsConfig.Credentials.class);
        when(credentials.getSecret()).thenReturn("secret");
        when(propsConfig.getCredentials()).thenReturn(credentials);

        KeycloakClientConfig config = new KeycloakClientConfig(propsConfig);
        Keycloak keycloak = config.keycloak();

        assertNotNull(keycloak);
    }
}
