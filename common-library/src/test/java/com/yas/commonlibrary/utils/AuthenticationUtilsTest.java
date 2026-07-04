package com.yas.commonlibrary.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.AccessDeniedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class AuthenticationUtilsTest {

    private SecurityContext securityContext;
    private Authentication authentication;

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
    void extractUserId_Success() {
        JwtAuthenticationToken jwtAuthenticationToken = mock(JwtAuthenticationToken.class);
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("user-id");
        when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
        when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);

        String userId = AuthenticationUtils.extractUserId();

        assertEquals("user-id", userId);
    }

    @Test
    void extractUserId_Anonymous_ThrowsException() {
        AnonymousAuthenticationToken anonymousToken = mock(AnonymousAuthenticationToken.class);
        when(securityContext.getAuthentication()).thenReturn(anonymousToken);

        assertThrows(AccessDeniedException.class, AuthenticationUtils::extractUserId);
    }

    @Test
    void extractJwt_Success() {
        Authentication auth = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("jwt-token");
        when(auth.getPrincipal()).thenReturn(jwt);
        when(securityContext.getAuthentication()).thenReturn(auth);

        String jwtToken = AuthenticationUtils.extractJwt();

        assertEquals("jwt-token", jwtToken);
    }

    @Test
    void getAuthentication_Success() {
        authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        Authentication result = AuthenticationUtils.getAuthentication();

        assertEquals(authentication, result);
    }
}
