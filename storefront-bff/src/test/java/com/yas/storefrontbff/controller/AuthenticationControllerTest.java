package com.yas.storefrontbff.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.storefrontbff.viewmodel.AuthenticatedUserVm;
import com.yas.storefrontbff.viewmodel.AuthenticationInfoVm;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

class AuthenticationControllerTest {

    private AuthenticationController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthenticationController();
    }

    @Test
    void user_whenPrincipalIsNull_shouldReturnNotAuthenticated() {
        ResponseEntity<AuthenticationInfoVm> response = controller.user(null);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isAuthenticated()).isFalse();
        assertThat(response.getBody().authenticatedUser()).isNull();
    }

    @Test
    void user_whenPrincipalHasUsername_shouldReturnAuthenticated() {
        OAuth2User principal = mock(OAuth2User.class);
        when(principal.getAttribute("preferred_username")).thenReturn("testuser");

        ResponseEntity<AuthenticationInfoVm> response = controller.user(principal);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isAuthenticated()).isTrue();
        assertThat(response.getBody().authenticatedUser()).isNotNull();
        assertThat(response.getBody().authenticatedUser().username()).isEqualTo("testuser");
    }

    @Test
    void user_whenPrincipalHasNullUsername_shouldStillReturnAuthenticated() {
        OAuth2User principal = mock(OAuth2User.class);
        when(principal.getAttribute("preferred_username")).thenReturn(null);

        ResponseEntity<AuthenticationInfoVm> response = controller.user(principal);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isAuthenticated()).isTrue();
        assertThat(response.getBody().authenticatedUser().username()).isNull();
    }

    @Test
    void user_whenPrincipalExists_shouldReturnCorrectUserInfo() {
        OAuth2User principal = mock(OAuth2User.class);
        when(principal.getAttribute("preferred_username")).thenReturn("admin@yas.com");

        ResponseEntity<AuthenticationInfoVm> response = controller.user(principal);

        assertThat(response.getBody().authenticatedUser().username()).isEqualTo("admin@yas.com");
    }
}
