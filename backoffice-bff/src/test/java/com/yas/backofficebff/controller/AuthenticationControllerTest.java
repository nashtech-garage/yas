package com.yas.backofficebff.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.backofficebff.viewmodel.AuthenticatedUser;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

class AuthenticationControllerTest {

    @Test
    void user_whenPreferredUsernamePresent_returnsAuthenticatedUser() {
        OAuth2User principal = mock(OAuth2User.class);
        when(principal.getAttribute("preferred_username")).thenReturn("admin");

        AuthenticationController controller = new AuthenticationController();
        ResponseEntity<AuthenticatedUser> response = controller.user(principal);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals("admin", response.getBody().username());
        assertEquals(200, response.getStatusCode().value());
    }
}
