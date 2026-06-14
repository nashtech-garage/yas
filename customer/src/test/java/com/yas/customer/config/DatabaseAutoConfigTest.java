package com.yas.customer.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

class DatabaseAutoConfigTest {

    private DatabaseAutoConfig databaseAutoConfig;

    @BeforeEach
    void setUp() {
        databaseAutoConfig = new DatabaseAutoConfig();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void auditorAware_WhenAuthenticationIsNull_ShouldReturnEmptyString() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        AuditorAware<String> auditorAware = databaseAutoConfig.auditorAware();
        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();

        assertTrue(currentAuditor.isPresent());
        assertEquals("", currentAuditor.get());
    }

    @Test
    void auditorAware_WhenAuthenticationIsNotNull_ShouldReturnUsername() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        AuditorAware<String> auditorAware = databaseAutoConfig.auditorAware();
        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();

        assertTrue(currentAuditor.isPresent());
        assertEquals("testuser", currentAuditor.get());
    }
}
