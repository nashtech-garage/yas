package com.yas.backofficebff.config;

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
        // SecurityConfig requires ReactiveClientRegistrationRepository,
        // but we only test the package-private utility method directly.
        securityConfig = new SecurityConfig(null);
    }

    @Test
    void generateAuthoritiesFromClaim_withRoles_shouldAddRolePrefix() {
        List<String> roles = List.of("ADMIN", "USER", "MANAGER");

        Collection<GrantedAuthority> authorities = securityConfig.generateAuthoritiesFromClaim(roles);

        assertThat(authorities).hasSize(3);
        assertThat(authorities)
            .extracting(GrantedAuthority::getAuthority)
            .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER", "ROLE_MANAGER");
    }

    @Test
    void generateAuthoritiesFromClaim_withSingleRole_shouldReturnOneAuthority() {
        List<String> roles = List.of("ADMIN");

        Collection<GrantedAuthority> authorities = securityConfig.generateAuthoritiesFromClaim(roles);

        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void generateAuthoritiesFromClaim_withEmptyRoles_shouldReturnEmptyCollection() {
        Collection<GrantedAuthority> authorities = securityConfig.generateAuthoritiesFromClaim(List.of());

        assertThat(authorities).isEmpty();
    }

    @Test
    void generateAuthoritiesFromClaim_rolePreservesCaseSensitivity() {
        List<String> roles = List.of("admin", "SuperUser");

        Collection<GrantedAuthority> authorities = securityConfig.generateAuthoritiesFromClaim(roles);

        assertThat(authorities)
            .extracting(GrantedAuthority::getAuthority)
            .containsExactlyInAnyOrder("ROLE_admin", "ROLE_SuperUser");
    }
}
