package com.yas.rating.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class SecurityContextUtils {

    private SecurityContextUtils() {
    }

    public static void setUpSecurityContext(String userName) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(userName);
        Jwt jwt = mock(Jwt.class);
        when(auth.getPrincipal()).thenReturn(jwt);
        when(jwt.getTokenValue()).thenReturn("token");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

}
