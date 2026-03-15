package com.yas.promotion.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class AuthenticationUtilsTest {

    @Test
    void testExtractUserId_whenExistsSubject_returnCurrentUserId() {

        JwtAuthenticationToken auth = mock(JwtAuthenticationToken.class);
        Jwt jwt = mock(Jwt.class);
        when(auth.getToken()).thenReturn(jwt);
        when(jwt.getSubject()).thenReturn("testUserId");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        String subject = AuthenticationUtils.extractUserId();
        assertEquals("testUserId", subject);
    }

    @Test
    void testExtractUserId_withAnonymousAuthentication_throwAccessDeniedException() {

        Authentication auth = mock(AnonymousAuthenticationToken.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(AccessDeniedException.class, AuthenticationUtils::extractUserId);
    }

    @Test
    void testExtractJwt() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("testJwtToken");
        
        JwtAuthenticationToken auth = mock(JwtAuthenticationToken.class);
        when(auth.getPrincipal()).thenReturn(jwt);
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        String token = AuthenticationUtils.extractJwt();
        assertEquals("testJwtToken", token);
    }

    @Test
    void testPrivateConstructor() throws Exception {
        java.lang.reflect.Constructor<AuthenticationUtils> constructor = AuthenticationUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertNotNull(constructor.newInstance());
    }
}
