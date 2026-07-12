package com.yas.backofficebff.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void springSecurityFilterChain_shouldThrowException_whenClientRegistrationRepositoryIsMissingInHttpContext() {
        ServerHttpSecurity http = ServerHttpSecurity.http();

        assertThrows(IllegalArgumentException.class, () -> securityConfig.springSecurityFilterChain(http));
    }

    @Test
    void generateAuthoritiesFromClaim_shouldPrefixRoleForEachClaim() {
        var result = securityConfig.generateAuthoritiesFromClaim(List.of("ADMIN", "STAFF"));

        Set<String> values = result.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());

        assertEquals(Set.of("ROLE_ADMIN", "ROLE_STAFF"), values);
    }

    @Test
    void generateAuthoritiesFromClaim_shouldReturnEmptyCollection_whenInputIsEmpty() {
        var result = securityConfig.generateAuthoritiesFromClaim(List.of());

        assertTrue(result.isEmpty());
    }

    @Test
    void userAuthoritiesMapperForKeycloak_shouldMapRoles_whenRealmAccessExists() {
        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        OAuth2UserAuthority authority = new OAuth2UserAuthority(Map.of(
            "realm_access", Map.of("roles", List.of("ADMIN", "OPS"))
        ));

        Set<String> mappedValues = mapper.mapAuthorities(List.of(authority)).stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());

        assertEquals(Set.of("ROLE_ADMIN", "ROLE_OPS"), mappedValues);
    }

    @Test
    void userAuthoritiesMapperForKeycloak_shouldReturnEmpty_whenRealmAccessMissing() {
        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        OAuth2UserAuthority authority = new OAuth2UserAuthority(Map.of("preferred_username", "admin"));

        var mapped = mapper.mapAuthorities(List.of(authority));

        assertTrue(mapped.isEmpty());
    }

    @Test
    void userAuthoritiesMapperForKeycloak_shouldThrowException_whenRolesAreInvalidType() {
        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        OAuth2UserAuthority authority = new OAuth2UserAuthority(Map.of(
            "realm_access", Map.of("roles", "ADMIN")
        ));

        assertThrows(ClassCastException.class, () -> mapper.mapAuthorities(List.of(authority)));
    }

    @Test
    void userAuthoritiesMapperForKeycloak_shouldThrowException_whenAuthoritiesAreEmpty() {
        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();

        assertThrows(java.util.NoSuchElementException.class, () -> mapper.mapAuthorities(List.of()));
    }
}
