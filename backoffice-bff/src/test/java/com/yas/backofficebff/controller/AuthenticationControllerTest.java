package com.yas.backofficebff.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.backofficebff.viewmodel.AuthenticatedUser;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

class AuthenticationControllerTest {

    private final AuthenticationController controller = new AuthenticationController();

    @Test
    void user_shouldReturnAuthenticatedUser() {
        OAuth2User principal = mock(OAuth2User.class);
        when(principal.getAttribute("preferred_username")).thenReturn("admin");
        
        ResponseEntity<AuthenticatedUser> response = controller.user(principal);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("admin", response.getBody().username());
    }
}
