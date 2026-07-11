package com.yas.backofficebff.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig(null);

    @Test
    void testGenerateAuthoritiesFromClaim() {
        Collection<String> roles = List.of("ADMIN", "USER");
        Collection<GrantedAuthority> authorities = securityConfig.generateAuthoritiesFromClaim(roles);

        assertEquals(2, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testUserAuthoritiesMapperForKeycloak_OidcUser() {
        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();

        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", List.of("ADMIN"));
        Map<String, Object> claims = new HashMap<>();
        claims.put("realm_access", realmAccess);

        OidcUserInfo userInfo = new OidcUserInfo(claims);
        OidcIdToken idToken = new OidcIdToken("token", Instant.now(), Instant.now().plusSeconds(60), Map.of("sub", "user"));
        OidcUserAuthority authority = new OidcUserAuthority(idToken, userInfo);

        Collection<? extends GrantedAuthority> mappedAuthorities = mapper.mapAuthorities(Set.of(authority));

        assertEquals(1, mappedAuthorities.size());
        assertEquals("ROLE_ADMIN", mappedAuthorities.iterator().next().getAuthority());
    }

    @Test
    void testUserAuthoritiesMapperForKeycloak_OAuth2User() {
        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();

        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", List.of("USER"));
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("realm_access", realmAccess);

        OAuth2UserAuthority authority = new OAuth2UserAuthority(attributes);

        Collection<? extends GrantedAuthority> mappedAuthorities = mapper.mapAuthorities(Set.of(authority));

        assertEquals(1, mappedAuthorities.size());
        assertEquals("ROLE_USER", mappedAuthorities.iterator().next().getAuthority());
    }
}
