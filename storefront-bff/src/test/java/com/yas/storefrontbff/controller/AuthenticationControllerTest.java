package com.yas.storefrontbff.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.yas.storefrontbff.viewmodel.AuthenticationInfoVm;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

class AuthenticationControllerTest {

    private final AuthenticationController authenticationController = new AuthenticationController();

    @Test
    void user_WhenPrincipalIsNull_ShouldReturnUnauthenticatedInfo() {
        ResponseEntity<AuthenticationInfoVm> response = authenticationController.user(null);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isAuthenticated()).isFalse();
        assertThat(response.getBody().authenticatedUser()).isNull();
    }

    @Test
    void user_WhenPrincipalExists_ShouldReturnAuthenticatedInfo() {
        OAuth2User principal = Mockito.mock(OAuth2User.class);
        when(principal.getAttribute("preferred_username")).thenReturn("demo.user");

        ResponseEntity<AuthenticationInfoVm> response = authenticationController.user(principal);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isAuthenticated()).isTrue();
        assertThat(response.getBody().authenticatedUser()).isNotNull();
        assertThat(response.getBody().authenticatedUser().username()).isEqualTo("demo.user");
    }
}
