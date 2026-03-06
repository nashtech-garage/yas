package com.yas.cart.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DatabaseAutoConfigTest {

    private DatabaseAutoConfig databaseAutoConfig;

    @BeforeEach
    void setUp() {
        databaseAutoConfig = new DatabaseAutoConfig();
        SecurityContextHolder.clearContext();
    }

    @Test
    void testAuditorAware_whenAuthenticationExists_shouldReturnUsername() {
        // Given
        String expectedUsername = "testuser";
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        
        when(authentication.getName()).thenReturn(expectedUsername);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // When
        AuditorAware<String> auditorAware = databaseAutoConfig.auditorAware();
        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();

        // Then
        assertTrue(currentAuditor.isPresent());
        assertEquals(expectedUsername, currentAuditor.get());
    }

    @Test
    void testAuditorAware_whenAuthenticationIsNull_shouldReturnEmptyString() {
        // Given
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // When
        AuditorAware<String> auditorAware = databaseAutoConfig.auditorAware();
        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();

        // Then
        assertTrue(currentAuditor.isPresent());
        assertEquals("", currentAuditor.get());
    }

    @Test
    void testAuditorAware_whenSecurityContextHolderHasNoContext_shouldReturnEmptyString() {
        // Given
        SecurityContextHolder.clearContext();

        // When
        AuditorAware<String> auditorAware = databaseAutoConfig.auditorAware();
        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();

        // Then
        assertTrue(currentAuditor.isPresent());
        assertEquals("", currentAuditor.get());
    }

    @Test
    void testAuditorAware_shouldReturnNonNullBean() {
        // When
        AuditorAware<String> auditorAware = databaseAutoConfig.auditorAware();

        // Then
        assertNotNull(auditorAware);
    }
}
