package com.yas.rating.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.AccessDeniedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class AuthenticationUtilsTest {

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
}
