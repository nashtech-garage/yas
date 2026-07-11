package com.yas.promotion.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class AuthenticationUtilsTest {

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void extractUserId_WhenAuthenticated_ShouldReturnUserId() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("user1");
        JwtAuthenticationToken authentication = mock(JwtAuthenticationToken.class);
        when(authentication.getToken()).thenReturn(jwt);
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String userId = AuthenticationUtils.extractUserId();
        assertEquals("user1", userId);
    }

    @Test
    void extractUserId_WhenAnonymous_ShouldThrowAccessDeniedException() {
        AnonymousAuthenticationToken authentication = mock(AnonymousAuthenticationToken.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> AuthenticationUtils.extractUserId());
        
        assertEquals("ACCESS_DENIED", exception.getMessage());
    }

    @Test
    void extractJwt_WhenAuthenticated_ShouldReturnJwtToken() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("mock-jwt-token");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String jwtToken = AuthenticationUtils.extractJwt();
        assertEquals("mock-jwt-token", jwtToken);
    }
}
