package com.yas.backofficebff.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

class SecurityConfigTest {

    private final SecurityConfig securityConfig =
        new SecurityConfig(null);

    @Test
    void testGenerateAuthoritiesFromClaim_shouldMapRolesToGrantedAuthorities() {
        List<String> roles = List.of("ADMIN", "USER");

        Collection<GrantedAuthority> authorities = securityConfig.generateAuthoritiesFromClaim(roles);

        assertNotNull(authorities);
        assertEquals(2, authorities.size());
        assertTrue(authorities.stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(authorities.stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testGenerateAuthoritiesFromClaim_shouldReturnEmptyForEmptyRoles() {
        Collection<GrantedAuthority> authorities =
            securityConfig.generateAuthoritiesFromClaim(List.of());

        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }
}
