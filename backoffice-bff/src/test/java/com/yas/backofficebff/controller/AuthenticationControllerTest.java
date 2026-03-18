package com.yas.backofficebff.controller;

import com.yas.backofficebff.viewmodel.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    private AuthenticationController authenticationController;

    @Mock
    private OAuth2User oAuth2User;

    @BeforeEach
    void setUp() {
        authenticationController = new AuthenticationController();
    }

    @Test
    void user_shouldReturnAuthenticatedUserWithUsername() {
        when(oAuth2User.getAttribute("preferred_username")).thenReturn("john_doe");

        ResponseEntity<AuthenticatedUser> response = authenticationController.user(oAuth2User);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().username()).isEqualTo("john_doe");
    }

    @Test
    void user_shouldReturnOkStatus() {
        when(oAuth2User.getAttribute("preferred_username")).thenReturn("admin");

        ResponseEntity<AuthenticatedUser> response = authenticationController.user(oAuth2User);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void user_shouldHandleNullUsername() {
        when(oAuth2User.getAttribute("preferred_username")).thenReturn(null);

        ResponseEntity<AuthenticatedUser> response = authenticationController.user(oAuth2User);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().username()).isNull();
    }

    @Test
    void user_shouldWrapUsernameInAuthenticatedUser() {
        String expectedUsername = "test_user@example.com";
        when(oAuth2User.getAttribute("preferred_username")).thenReturn(expectedUsername);

        ResponseEntity<AuthenticatedUser> response = authenticationController.user(oAuth2User);

        assertThat(response.getBody()).isEqualTo(new AuthenticatedUser(expectedUsername));
    }
}