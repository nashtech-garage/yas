package com.yas.commonlibrary.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.constants.ApiConstant;
import com.yas.commonlibrary.exception.AccessDeniedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class AuthenticationUtilsTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAuthentication_ReturnsCurrentAuthentication() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Authentication result = AuthenticationUtils.getAuthentication();

        assertSame(authentication, result);
    }

    @Test
    void extractJwt_ReturnsTokenValue() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("jwt-token");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        String result = AuthenticationUtils.extractJwt();

        assertEquals("jwt-token", result);
    }

    @Test
    void extractUserId_WhenAnonymous_ThrowsAccessDeniedException() {
        AnonymousAuthenticationToken anonymousAuthenticationToken =
                new AnonymousAuthenticationToken("key", "anonymousUser", java.util.List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(anonymousAuthenticationToken);
        SecurityContextHolder.setContext(securityContext);

        AccessDeniedException exception =
                assertThrows(AccessDeniedException.class, AuthenticationUtils::extractUserId);

        assertEquals(ApiConstant.ACCESS_DENIED, exception.getMessage());
    }

    @Test
    void extractUserId_WhenJwtAuthentication_ReturnsSubject() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("user-123");

        JwtAuthenticationToken jwtAuthenticationToken = mock(JwtAuthenticationToken.class);
        when(jwtAuthenticationToken.getToken()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
        SecurityContextHolder.setContext(securityContext);

        String result = AuthenticationUtils.extractUserId();

        assertEquals("user-123", result);
    }
}
