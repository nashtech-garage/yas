package com.yas.backofficebff.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.backofficebff.viewmodel.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

class AuthenticationControllerTest {

    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        authenticationController = new AuthenticationController();
    }

    @Test
    void user_withValidPrincipal_shouldReturnOkWithUsername() {
        OAuth2User principal = mock(OAuth2User.class);
        when(principal.getAttribute("preferred_username")).thenReturn("john_doe");

        ResponseEntity<AuthenticatedUser> response = authenticationController.user(principal);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().username()).isEqualTo("john_doe");
    }

    @Test
    void user_withDifferentUsername_shouldReturnCorrectUsername() {
        OAuth2User principal = mock(OAuth2User.class);
        when(principal.getAttribute("preferred_username")).thenReturn("admin_user");

        ResponseEntity<AuthenticatedUser> response = authenticationController.user(principal);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().username()).isEqualTo("admin_user");
    }

    @Test
    void user_withNullUsername_shouldReturnOkWithNullUsername() {
        OAuth2User principal = mock(OAuth2User.class);
        when(principal.getAttribute("preferred_username")).thenReturn(null);

        ResponseEntity<AuthenticatedUser> response = authenticationController.user(principal);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().username()).isNull();
    }
}
