package com.yas.rating.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.AccessDeniedException;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class AuthenticationUtilsTest {

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void extractUserId_whenAnonymousUser_throwsAccessDeniedException() {
        AnonymousAuthenticationToken auth = mock(AnonymousAuthenticationToken.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(AccessDeniedException.class, AuthenticationUtils::extractUserId);
    }

    @Test
    void extractUserId_whenAuthenticatedUser_returnsUserId() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("test-user-id");

        JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, Collections.emptyList(), "test-user-id");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        String userId = AuthenticationUtils.extractUserId();

        assertThat(userId).isEqualTo("test-user-id");
    }
}
