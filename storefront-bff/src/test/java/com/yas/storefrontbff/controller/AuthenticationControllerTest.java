package com.yas.storefrontbff.controller;

import com.yas.storefrontbff.viewmodel.AuthenticatedUserVm;
import com.yas.storefrontbff.viewmodel.AuthenticationInfoVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        authenticationController = new AuthenticationController();
    }

    // ═══════════════════════════════════════════════════════════════
    // user()
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /authentication")
    class UserEndpoint {

        @Test
        @DisplayName("principal = null → isAuthenticated=false, authenticatedUser=null")
        void whenPrincipalIsNull_shouldReturnUnauthenticated() {
            ResponseEntity<AuthenticationInfoVm> response = authenticationController.user(null);

            assertThat(response.getStatusCode().value()).isEqualTo(200);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().isAuthenticated()).isFalse();
            assertThat(response.getBody().authenticatedUser()).isNull();
        }

        @Test
        @DisplayName("principal hợp lệ → isAuthenticated=true, username đúng")
        void whenPrincipalIsPresent_shouldReturnAuthenticated() {
            OAuth2User principal = mock(OAuth2User.class);
            when(principal.getAttribute("preferred_username")).thenReturn("testuser");

            ResponseEntity<AuthenticationInfoVm> response = authenticationController.user(principal);

            assertThat(response.getStatusCode().value()).isEqualTo(200);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().isAuthenticated()).isTrue();
            assertThat(response.getBody().authenticatedUser()).isNotNull();
            assertThat(response.getBody().authenticatedUser().username()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("principal có preferred_username = null → username = null")
        void whenPreferredUsernameIsNull_shouldReturnAuthenticatedWithNullUsername() {
            OAuth2User principal = mock(OAuth2User.class);
            when(principal.getAttribute("preferred_username")).thenReturn(null);

            ResponseEntity<AuthenticationInfoVm> response = authenticationController.user(principal);

            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().isAuthenticated()).isTrue();
            assertThat(response.getBody().authenticatedUser().username()).isNull();
        }

        @Test
        @DisplayName("principal có nhiều attributes → chỉ lấy preferred_username")
        void whenPrincipalHasMultipleAttributes_shouldOnlyUsePreferredUsername() {
            OAuth2User principal = mock(OAuth2User.class);
            when(principal.getAttribute("preferred_username")).thenReturn("john_doe");
            when(principal.getAttributes()).thenReturn(Map.of(
                "preferred_username", "john_doe",
                "email", "john@example.com",
                "sub", "user-id-123"
            ));

            ResponseEntity<AuthenticationInfoVm> response = authenticationController.user(principal);

            assertThat(response.getBody().authenticatedUser().username()).isEqualTo("john_doe");
            verify(principal).getAttribute("preferred_username");
        }

        @Test
        @DisplayName("response không null và có status 200 OK")
        void responseStatusIsAlways200() {
            ResponseEntity<AuthenticationInfoVm> responseNull = authenticationController.user(null);
            assertThat(responseNull.getStatusCode()).isEqualTo(HttpStatus.OK);

            OAuth2User principal = mock(OAuth2User.class);
            when(principal.getAttribute("preferred_username")).thenReturn("someone");
            ResponseEntity<AuthenticationInfoVm> responseAuth = authenticationController.user(principal);
            assertThat(responseAuth.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("principal null → body.authenticatedUser() là null (không NPE)")
        void whenPrincipalIsNull_bodyNeverThrowsNPE() {
            ResponseEntity<AuthenticationInfoVm> response = authenticationController.user(null);

            // Không ném exception, body tồn tại
            assertThat(response).isNotNull();
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().isAuthenticated()).isFalse();
            assertThat(response.getBody().authenticatedUser()).isNull();
        }

        @Test
        @DisplayName("principal với empty attributes map → username = null")
        void whenPrincipalHasEmptyAttributes_shouldReturnAuthenticatedWithNullUsername() {
            OAuth2User principal = mock(OAuth2User.class);
            when(principal.getAttribute("preferred_username")).thenReturn(null);
            when(principal.getAttributes()).thenReturn(Collections.emptyMap());

            ResponseEntity<AuthenticationInfoVm> response = authenticationController.user(principal);

            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().isAuthenticated()).isTrue();
            assertThat(response.getBody().authenticatedUser()).isNotNull();
            assertThat(response.getBody().authenticatedUser().username()).isNull();
        }

        @Test
        @DisplayName("preferred_username là chuỗi rỗng → username là empty string")
        void whenPreferredUsernameIsEmpty_shouldReturnEmptyUsername() {
            OAuth2User principal = mock(OAuth2User.class);
            when(principal.getAttribute("preferred_username")).thenReturn("");

            ResponseEntity<AuthenticationInfoVm> response = authenticationController.user(principal);

            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().isAuthenticated()).isTrue();
            assertThat(response.getBody().authenticatedUser().username()).isEmpty();
        }

        @Test
        @DisplayName("getAttribute chỉ được gọi đúng 1 lần với key 'preferred_username'")
        void getAttributeCalledOnceWithCorrectKey() {
            OAuth2User principal = mock(OAuth2User.class);
            when(principal.getAttribute("preferred_username")).thenReturn("alice");

            authenticationController.user(principal);

            verify(principal, times(1)).getAttribute("preferred_username");
            verifyNoMoreInteractions(principal);
        }
    }
}