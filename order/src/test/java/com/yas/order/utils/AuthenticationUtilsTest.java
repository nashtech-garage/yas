package com.yas.order.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class AuthenticationUtilsTest {

    @Test
    void testGetCurrentUserId_existsCurrentUserId_returnCurrentUserId() {

        JwtAuthenticationToken auth = mock(JwtAuthenticationToken.class);
        Jwt jwt = mock(Jwt.class);
        when(auth.getToken()).thenReturn(jwt);
        when(jwt.getSubject()).thenReturn("testUserId");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        String au = AuthenticationUtils.getCurrentUserId();
        assertEquals("testUserId", au);
    }
}