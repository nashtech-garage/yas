package com.yas.storefrontbff.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.storefrontbff.viewmodel.AuthenticationInfoVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

class AuthenticationControllerTest {

    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        authenticationController = new AuthenticationController();
    }

    @Test
    void user_whenPrincipalIsNull_shouldReturnUnauthenticated() {
        ResponseEntity<AuthenticationInfoVm> response = authenticationController.user(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isAuthenticated()).isFalse();
        assertThat(response.getBody().authenticatedUser()).isNull();
    }

    @Test
    void user_whenPrincipalIsNotNull_shouldReturnAuthenticated() {
        OAuth2User principal = mock(OAuth2User.class);
        when(principal.getAttribute("preferred_username")).thenReturn("testuser");

        ResponseEntity<AuthenticationInfoVm> response = authenticationController.user(principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isAuthenticated()).isTrue();
        assertThat(response.getBody().authenticatedUser().username()).isEqualTo("testuser");
    }
}

