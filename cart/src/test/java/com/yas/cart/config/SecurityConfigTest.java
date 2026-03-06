package com.yas.cart.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
    }

    @Test
    void testJwtAuthenticationConverter_shouldReturnNonNullConverter() {
        // When
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverterForKeycloak();

        // Then
        assertNotNull(converter);
    }

    @Test
    void testJwtAuthenticationConverter_whenJwtHasRoles_shouldConvertToGrantedAuthorities() {
        // Given
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverterForKeycloak();
        
        Map<String, Object> claims = new HashMap<>();
        Map<String, Collection<String>> realmAccess = new HashMap<>();
        List<String> roles = Arrays.asList("CUSTOMER", "ADMIN");
        realmAccess.put("roles", roles);
        claims.put("realm_access", realmAccess);
        claims.put("sub", "user123");
        
        Jwt jwt = new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            Map.of("alg", "none"),
            claims
        );

        // When
        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        // Then
        assertNotNull(authorities);
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void testJwtAuthenticationConverter_whenJwtHasSingleRole_shouldConvertCorrectly() {
        // Given
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverterForKeycloak();
        
        Map<String, Object> claims = new HashMap<>();
        Map<String, Collection<String>> realmAccess = new HashMap<>();
        List<String> roles = Collections.singletonList("USER");
        realmAccess.put("roles", roles);
        claims.put("realm_access", realmAccess);
        claims.put("sub", "user456");
        
        Jwt jwt = new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            Map.of("alg", "none"),
            claims
        );

        // When
        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        // Then
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testJwtAuthenticationConverter_whenJwtHasEmptyRoles_shouldReturnEmptyAuthorities() {
        // Given
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverterForKeycloak();
        
        Map<String, Object> claims = new HashMap<>();
        Map<String, Collection<String>> realmAccess = new HashMap<>();
        realmAccess.put("roles", Collections.emptyList());
        claims.put("realm_access", realmAccess);
        claims.put("sub", "user789");
        
        Jwt jwt = new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            Map.of("alg", "none"),
            claims
        );

        // When
        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        // Then
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }
}
