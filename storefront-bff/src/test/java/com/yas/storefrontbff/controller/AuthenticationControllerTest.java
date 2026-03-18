package com.yas.storefrontbff.controller;

import com.yas.storefrontbff.viewmodel.AuthenticatedUserVm;
import com.yas.storefrontbff.viewmodel.AuthenticationInfoVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    private AuthenticationController controller;

    @Mock
    private OAuth2User oAuth2User;

    @BeforeEach
    void setUp() {
        controller = new AuthenticationController();
    }

    // -----------------------------------------------------------------------
    // Authenticated path
    // -----------------------------------------------------------------------

    @Test
    void user_withPrincipal_shouldReturnAuthenticatedTrue() {
        when(oAuth2User.getAttribute("preferred_username")).thenReturn("alice");

        ResponseEntity<AuthenticationInfoVm> response = controller.user(oAuth2User);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isAuthenticated()).isTrue();
    }

    @Test
    void user_withPrincipal_shouldReturnCorrectUsername() {
        when(oAuth2User.getAttribute("preferred_username")).thenReturn("alice");

        ResponseEntity<AuthenticationInfoVm> response = controller.user(oAuth2User);

        assertThat(response.getBody().authenticatedUser()).isNotNull();
        assertThat(response.getBody().authenticatedUser().username()).isEqualTo("alice");
    }

    @Test
    void user_withPrincipal_shouldWrapUsernameInAuthenticatedUserVm() {
        when(oAuth2User.getAttribute("preferred_username")).thenReturn("bob");

        ResponseEntity<AuthenticationInfoVm> response = controller.user(oAuth2User);

        assertThat(response.getBody().authenticatedUser())
                .isEqualTo(new AuthenticatedUserVm("bob"));
    }

    @Test
    void user_withPrincipalHavingNullUsername_shouldReturnNullUsername() {
        when(oAuth2User.getAttribute("preferred_username")).thenReturn(null);

        ResponseEntity<AuthenticationInfoVm> response = controller.user(oAuth2User);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isAuthenticated()).isTrue();
        assertThat(response.getBody().authenticatedUser().username()).isNull();
    }

    @Test
    void user_withPrincipal_shouldReturnExpectedInfoVm() {
        when(oAuth2User.getAttribute("preferred_username")).thenReturn("admin");

        ResponseEntity<AuthenticationInfoVm> response = controller.user(oAuth2User);

        AuthenticationInfoVm expected = new AuthenticationInfoVm(true, new AuthenticatedUserVm("admin"));
        assertThat(response.getBody()).isEqualTo(expected);
    }

    // -----------------------------------------------------------------------
    // Unauthenticated path (null principal)
    // -----------------------------------------------------------------------

    @Test
    void user_withNullPrincipal_shouldReturnAuthenticatedFalse() {
        ResponseEntity<AuthenticationInfoVm> response = controller.user(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isAuthenticated()).isFalse();
    }

    @Test
    void user_withNullPrincipal_shouldReturnNullAuthenticatedUser() {
        ResponseEntity<AuthenticationInfoVm> response = controller.user(null);

        assertThat(response.getBody().authenticatedUser()).isNull();
    }

    @Test
    void user_withNullPrincipal_shouldReturnExpectedInfoVm() {
        ResponseEntity<AuthenticationInfoVm> response = controller.user(null);

        AuthenticationInfoVm expected = new AuthenticationInfoVm(false, null);
        assertThat(response.getBody()).isEqualTo(expected);
    }
}