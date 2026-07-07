package com.yas.customer.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DatabaseAutoConfigTest {

    private DatabaseAutoConfig config;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        config = new DatabaseAutoConfig();
        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testAuditorAware_whenAuthIsNull_returnEmptyString() {
        when(securityContext.getAuthentication()).thenReturn(null);
        
        AuditorAware<String> auditorAware = config.auditorAware();
        Optional<String> result = auditorAware.getCurrentAuditor();
        
        assertEquals(Optional.of(""), result);
    }

    @Test
    void testAuditorAware_whenAuthIsNotNull_returnAuthName() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test-user");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        
        AuditorAware<String> auditorAware = config.auditorAware();
        Optional<String> result = auditorAware.getCurrentAuditor();
        
        assertEquals(Optional.of("test-user"), result);
    }
}
