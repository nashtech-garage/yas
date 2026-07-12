package com.yas.backofficebff.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.yas.backofficebff.viewmodel.AuthenticatedUser;
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
    void user_shouldReturnAuthenticatedUser_whenPrincipalContainsPreferredUsername() {
        when(principal.getAttribute("preferred_username")).thenReturn("admin");

        ResponseEntity<AuthenticatedUser> response = authenticationController.user(principal);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("admin", response.getBody().username());
    }

    @Test
    void user_shouldThrowException_whenPrincipalIsNull() {
        assertThrows(NullPointerException.class, () -> authenticationController.user(null));
    }

    @Test
    void user_shouldPropagateException_whenPrincipalAttributeAccessFails() {
        when(principal.getAttribute("preferred_username"))
            .thenThrow(new IllegalStateException("unable to load username"));

        assertThrows(IllegalStateException.class, () -> authenticationController.user(principal));
    }
}
