package com.yas.storefrontbff.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.yas.storefrontbff.viewmodel.AuthenticationInfoVm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private OAuth2User principal;

    @Test
    void user_shouldReturnUnauthenticatedResponse_whenPrincipalIsNull() {
        ResponseEntity<AuthenticationInfoVm> response = authenticationController.user(null);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isAuthenticated());
        assertEquals(null, response.getBody().authenticatedUser());
    }

    @Test
    void user_shouldReturnAuthenticatedResponse_whenPrincipalExists() {
        when(principal.getAttribute("preferred_username")).thenReturn("alice");

        ResponseEntity<AuthenticationInfoVm> response = authenticationController.user(principal);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isAuthenticated());
        assertNotNull(response.getBody().authenticatedUser());
        assertEquals("alice", response.getBody().authenticatedUser().username());
    }

    @Test
    void user_shouldPropagateException_whenPrincipalAttributeAccessFails() {
        when(principal.getAttribute("preferred_username"))
            .thenThrow(new IllegalStateException("Unable to read principal"));

        assertThrows(IllegalStateException.class, () -> authenticationController.user(principal));
    }
}
