package com.yas.promotion.utils;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthenticationUtilsTest {

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testExtractUserId_AnonymousUser_ThrowsException() {
        SecurityContext context = mock(SecurityContext.class);
        AnonymousAuthenticationToken auth = mock(AnonymousAuthenticationToken.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        // Bắt lỗi nếu người dùng chưa đăng nhập
        assertThrows(AccessDeniedException.class, AuthenticationUtils::extractUserId);
    }

    @Test
    void testExtractUserId_JwtAuth_Success() {
        SecurityContext context = mock(SecurityContext.class);
        JwtAuthenticationToken auth = mock(JwtAuthenticationToken.class);
        Jwt jwt = mock(Jwt.class);

        when(jwt.getSubject()).thenReturn("user-uuid-123");
        when(auth.getToken()).thenReturn(jwt);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        String userId = AuthenticationUtils.extractUserId();
        assertEquals("user-uuid-123", userId);
    }

    @Test
    void testExtractJwt_Success() {
        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);

        when(jwt.getTokenValue()).thenReturn("header.payload.signature");
        when(auth.getPrincipal()).thenReturn(jwt);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        // Trích xuất thành công token
        String tokenValue = AuthenticationUtils.extractJwt();
        assertEquals("header.payload.signature", tokenValue);
    }
}