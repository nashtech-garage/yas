package com.yas.storefrontbff.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.storefrontbff.viewmodel.AuthenticationInfoVm;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

class AuthenticationControllerTest {

    private final AuthenticationController controller = new AuthenticationController();

    @Test
    void user_whenPrincipalIsNull_shouldReturnNotAuthenticated() {
        ResponseEntity<AuthenticationInfoVm> response = controller.user(null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isAuthenticated());
    }

    @Test
    void user_whenPrincipalIsNotNull_shouldReturnAuthenticated() {
        OAuth2User principal = mock(OAuth2User.class);
        when(principal.getAttribute("preferred_username")).thenReturn("user");
        
        ResponseEntity<AuthenticationInfoVm> response = controller.user(principal);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isAuthenticated());
        assertEquals("user", response.getBody().authenticatedUser().username());
    }
}
