package com.yas.cart.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SecurityContextUtils {

    private SecurityContextUtils() {
    }

    public static void setUpSecurityContext(String userName) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(userName);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

}
