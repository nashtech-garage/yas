package com.yas.paymentpaypal.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.paymentpaypal.exception.SignInRequiredException;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class AuthenticationUtilsTest {

    @Test
    void testGetCurrentUserId_whenExistsSubject_returnCurrentUserId() {

        JwtAuthenticationToken auth = mock(JwtAuthenticationToken.class);
        Jwt jwt = mock(Jwt.class);
        when(auth.getToken()).thenReturn(jwt);
        when(jwt.getSubject()).thenReturn("testUserId");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        String subject = AuthenticationUtils.getCurrentUserId();
        assertThat(subject).isEqualTo("testUserId");
    }

    @Test
    void testGetCurrentUserId_withAnonymousAuthentication_throwSignInRequiredException() {

        Authentication auth = mock(AnonymousAuthenticationToken.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(SignInRequiredException.class, AuthenticationUtils::getCurrentUserId);
    }

}