package com.yas.backofficebff.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.backofficebff.viewmodel.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

class AuthenticationControllerTest {

    private AuthenticationController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthenticationController();
    }

    @Nested
    class UserEndpointTest {

        @Test
        void testUser_whenValidPrincipal_shouldReturnAuthenticatedUser() {
            OAuth2User principal = mock(OAuth2User.class);
            when(principal.getAttribute("preferred_username")).thenReturn("testuser");

            ResponseEntity<AuthenticatedUser> response = controller.user(principal);

            assertNotNull(response);
            assertEquals(200, response.getStatusCode().value());
            assertNotNull(response.getBody());
            assertEquals("testuser", response.getBody().username());
        }

        @Test
        void testUser_whenUsernameIsNull_shouldReturnNullUsername() {
            OAuth2User principal = mock(OAuth2User.class);
            when(principal.getAttribute("preferred_username")).thenReturn(null);

            ResponseEntity<AuthenticatedUser> response = controller.user(principal);

            assertNotNull(response);
            assertEquals(200, response.getStatusCode().value());
            assertNotNull(response.getBody());
            assertNull(response.getBody().username());
        }

        @Test
        void testUser_whenDifferentUsername_shouldReturnCorrectUsername() {
            OAuth2User principal = mock(OAuth2User.class);
            when(principal.getAttribute("preferred_username")).thenReturn("admin@yas.local");

            ResponseEntity<AuthenticatedUser> response = controller.user(principal);

            assertNotNull(response);
            assertEquals("admin@yas.local", response.getBody().username());
        }
    }
}
