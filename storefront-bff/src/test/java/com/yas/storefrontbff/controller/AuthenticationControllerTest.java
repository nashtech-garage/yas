package com.yas.storefrontbff.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.storefrontbff.viewmodel.AuthenticationInfoVm;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

class AuthenticationControllerTest {

    private final AuthenticationController authenticationController = new AuthenticationController();

    @Test
    void user_WhenPrincipalIsNull_ReturnsUnauthenticatedResponse() {
        ResponseEntity<AuthenticationInfoVm> response = authenticationController.user(null);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isAuthenticated());
        assertEquals(null, response.getBody().authenticatedUser());
    }

    @Test
    void user_WhenPrincipalExists_ReturnsAuthenticatedResponse() {
        OAuth2User principal = mock(OAuth2User.class);
        when(principal.getAttribute("preferred_username")).thenReturn("john");

        ResponseEntity<AuthenticationInfoVm> response = authenticationController.user(principal);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isAuthenticated());
        assertNotNull(response.getBody().authenticatedUser());
        assertEquals("john", response.getBody().authenticatedUser().username());
    }
}
