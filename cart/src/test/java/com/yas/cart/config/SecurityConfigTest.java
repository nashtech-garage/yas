package com.yas.cart.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Unit tests for cart SecurityConfig.
 *
 * filterChain() is a @Bean that wires HttpSecurity rules — no branching logic,
 * cannot be unit-tested without a servlet container. Covered by integration tests.
 *
 * jwtAuthenticationConverterForKeycloak() contains real role-mapping logic and is
 * the primary target here. Tests invoke the real production method to ensure
 * SonarCloud line coverage on the actual source file.
 */
class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
    }

    // ──────────────────────────────────────────────────────────────────
    // jwtAuthenticationConverterForKeycloak — bean creation
    // ──────────────────────────────────────────────────────────────────

    @Test
    void testJwtAuthenticationConverterForKeycloak_whenCalled_shouldReturnNonNull() {
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverterForKeycloak();

        assertThat(converter).isNotNull();
    }

    // ──────────────────────────────────────────────────────────────────
    // Internal lambda — role mapping (invoked via converter.convert())
    // These tests execute the real production lines inside SecurityConfig.
    // ──────────────────────────────────────────────────────────────────

    @Test
    void testJwtGrantedAuthoritiesConverter_whenJwtHasTwoRoles_shouldReturnTwoGrantedAuthorities() {
        Jwt jwt = buildJwt(List.of("ADMIN", "CUSTOMER"));

        Collection<GrantedAuthority> authorities = convertAuthorities(jwt);

        assertThat(authorities).hasSize(2);
        assertThat(authorities)
            .extracting(GrantedAuthority::getAuthority)
            .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_CUSTOMER");
    }

    @Test
    void testJwtGrantedAuthoritiesConverter_whenJwtHasSingleRole_shouldReturnOneGrantedAuthority() {
        Jwt jwt = buildJwt(List.of("USER"));

        Collection<GrantedAuthority> authorities = convertAuthorities(jwt);

        assertThat(authorities).hasSize(1);
        assertThat(authorities)
            .extracting(GrantedAuthority::getAuthority)
            .containsExactly("ROLE_USER");
    }

    @Test
    void testJwtGrantedAuthoritiesConverter_whenRolesIsEmpty_shouldReturnEmptyCollection() {
        Jwt jwt = buildJwt(List.of());

        Collection<GrantedAuthority> authorities = convertAuthorities(jwt);

        assertThat(authorities).isEmpty();
    }

    @Test
    void testJwtGrantedAuthoritiesConverter_whenRoleHasSpecialChars_shouldPrefixCorrectly() {
        Jwt jwt = buildJwt(List.of("offline_access"));

        Collection<GrantedAuthority> authorities = convertAuthorities(jwt);

        assertThat(authorities)
            .extracting(GrantedAuthority::getAuthority)
            .containsExactly("ROLE_offline_access");
    }

    // ──────────────────────────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────────────────────────

    /**
     * Calls the real production jwtAuthenticationConverterForKeycloak() and invokes
     * convert(Jwt) on it so that every line inside the production lambda is executed.
     */
    /**
     * Returns only ROLE_* authorities from the converted token.
     * Spring Security 6.x automatically adds FactorGrantedAuthority[FACTOR_BEARER]
     * to every JwtAuthenticationToken — we exclude it to assert only the custom
     * role-mapping logic defined in the production SecurityConfig lambda.
     */
    private Collection<GrantedAuthority> convertAuthorities(Jwt jwt) {
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverterForKeycloak();
        JwtAuthenticationToken token = (JwtAuthenticationToken) converter.convert(jwt);
        return token.getAuthorities().stream()
            .filter(a -> a.getAuthority().startsWith("ROLE_"))
            .collect(Collectors.toList());
    }

    /**
     * Builds a real {@link Jwt} using the standard Spring Security builder.
     * Includes the "sub" claim required by JwtAuthenticationConverter's principal extraction.
     */
    private static Jwt buildJwt(List<String> roles) {
        return Jwt.withTokenValue("test-token")
            .header("alg", "RS256")
            .subject("test-user")
            .claim("realm_access", Map.of("roles", roles))
            .build();
    }
}
