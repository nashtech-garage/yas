package com.yas.rating.utils;

import com.yas.commonlibrary.exception.AccessDeniedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthenticationUtilsTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testExtractUserId_whenValidJwtToken_shouldReturnUserId() {
        // Given
        String expectedUserId = "user-123";
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(expectedUserId);

        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, Collections.emptyList());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // When
        String actualUserId = AuthenticationUtils.extractUserId();

        // Then
        assertThat(actualUserId).isEqualTo(expectedUserId);
    }

    @Test
    void testExtractUserId_whenAnonymousUser_shouldThrowAccessDeniedException() {
        // Given
        AnonymousAuthenticationToken authentication = new AnonymousAuthenticationToken(
                "key",
                "anonymous",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // When & Then
        assertThatThrownBy(() -> AuthenticationUtils.extractUserId())
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining(Constants.ErrorCode.ACCESS_DENIED);
    }

    @Test
    void testExtractUserId_whenDifferentUserId_shouldReturnCorrectUserId() {
        // Given
        String expectedUserId = "user-456";
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(expectedUserId);

        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, Collections.emptyList());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // When
        String actualUserId = AuthenticationUtils.extractUserId();

        // Then
        assertThat(actualUserId).isEqualTo(expectedUserId);
    }
}
