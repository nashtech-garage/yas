package com.yas.promotion.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class AuthenticationUtilsTest {

    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void extractUserId_WithAnonymousUser_ShouldThrowAccessDeniedException() {
        AnonymousAuthenticationToken anonymousToken = mock(AnonymousAuthenticationToken.class);
        when(securityContext.getAuthentication()).thenReturn(anonymousToken);

        assertThrows(AccessDeniedException.class, AuthenticationUtils::extractUserId);
    }

    @Test
    void extractUserId_WithJwtToken_ShouldReturnUserId() {
        JwtAuthenticationToken jwtToken = mock(JwtAuthenticationToken.class);
        Jwt jwt = mock(Jwt.class);
        when(securityContext.getAuthentication()).thenReturn(jwtToken);
        when(jwtToken.getToken()).thenReturn(jwt);
        when(jwt.getSubject()).thenReturn("user123");

        String userId = AuthenticationUtils.extractUserId();

        assertEquals("user123", userId);
    }

    @Test
    void extractJwt_WithJwtPrincipal_ShouldReturnTokenValue() {
        Authentication authentication = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getTokenValue()).thenReturn("token123");

        String token = AuthenticationUtils.extractJwt();

        assertEquals("token123", token);
    }
}
