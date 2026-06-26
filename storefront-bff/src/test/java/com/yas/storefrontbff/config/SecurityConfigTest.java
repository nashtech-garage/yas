package com.yas.storefrontbff.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        // SecurityConfig requires a ReactiveClientRegistrationRepository in constructor
        // We pass null for unit testing the generateAuthoritiesFromClaim method only
        securityConfig = new SecurityConfig(null);
    }

    @Test
    void generateAuthoritiesFromClaim_withSingleRole_shouldReturnSingleAuthority() {
        List<String> roles = List.of("ADMIN");

        Collection<GrantedAuthority> authorities = securityConfig.generateAuthoritiesFromClaim(roles);

        assertThat(authorities).hasSize(1);
        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
            .containsExactly("ROLE_ADMIN");
    }

    @Test
    void generateAuthoritiesFromClaim_withMultipleRoles_shouldReturnAllAuthorities() {
        List<String> roles = List.of("ADMIN", "USER", "MANAGER");

        Collection<GrantedAuthority> authorities = securityConfig.generateAuthoritiesFromClaim(roles);

        assertThat(authorities).hasSize(3);
        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
            .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER", "ROLE_MANAGER");
    }

    @Test
    void generateAuthoritiesFromClaim_withEmptyRoles_shouldReturnEmptyCollection() {
        List<String> roles = List.of();

        Collection<GrantedAuthority> authorities = securityConfig.generateAuthoritiesFromClaim(roles);

        assertThat(authorities).isEmpty();
    }

    @Test
    void generateAuthoritiesFromClaim_rolesShouldHaveRolePrefix() {
        List<String> roles = List.of("customer");

        Collection<GrantedAuthority> authorities = securityConfig.generateAuthoritiesFromClaim(roles);

        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
            .isNotEmpty()
            .allMatch(auth -> auth.startsWith("ROLE_"));
    }

    @Test
    void generateAuthoritiesFromClaim_withLowercaseRole_shouldPrefixCorrectly() {
        List<String> roles = List.of("viewer");

        Collection<GrantedAuthority> authorities = securityConfig.generateAuthoritiesFromClaim(roles);

        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
            .containsExactly("ROLE_viewer");
    }
}
