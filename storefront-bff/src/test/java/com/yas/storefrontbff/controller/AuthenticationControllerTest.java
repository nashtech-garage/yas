package com.yas.storefrontbff.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.mockito.Mockito;

class AuthenticationControllerTest {

    private final AuthenticationController controller = new AuthenticationController();

    @Test
    void user_returnsUnauthenticatedWhenPrincipalIsNull() {
        ResponseEntity<?> response = controller.user(null);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();

        var body = (com.yas.storefrontbff.viewmodel.AuthenticationInfoVm) response.getBody();
        assertThat(body.isAuthenticated()).isFalse();
        assertThat(body.authenticatedUser()).isNull();
    }

    @Test
    void user_returnsAuthenticatedUserWhenPrincipalPresent() {
        OAuth2User principal = Mockito.mock(OAuth2User.class);
        when(principal.getAttribute("preferred_username")).thenReturn("alice");

        var response = controller.user(principal);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        var body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.isAuthenticated()).isTrue();
        assertThat(body.authenticatedUser()).isNotNull();
        assertThat(body.authenticatedUser().username()).isEqualTo("alice");
    }

    @Test
    void user_returnsAuthenticatedEvenWhenUsernameMissing() {
        OAuth2User principal = Mockito.mock(OAuth2User.class);
        when(principal.getAttribute("preferred_username")).thenReturn(null);

        var response = controller.user(principal);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        var body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.isAuthenticated()).isTrue();
        assertThat(body.authenticatedUser()).isNotNull();
        assertThat(body.authenticatedUser().username()).isNull();
    }
}
