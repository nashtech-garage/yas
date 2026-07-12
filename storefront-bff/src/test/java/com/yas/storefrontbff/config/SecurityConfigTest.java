package com.yas.storefrontbff.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    void generateAuthoritiesFromClaim_shouldPrefixRolesWithRole() {
        var authorities = securityConfig.generateAuthoritiesFromClaim(List.of("admin", "user"));

        var values = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

        assertEquals(List.of("ROLE_admin", "ROLE_user"), values);
    }

    @Test
    void userAuthoritiesMapperForKeycloak_shouldMapRoles_whenOAuth2AuthorityHasRealmAccess() {
        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        OAuth2UserAuthority authority = new OAuth2UserAuthority(Map.of(
            "realm_access", Map.of("roles", List.of("admin", "manager"))
        ));

        Set<String> mappedValues = mapper.mapAuthorities(List.of(authority)).stream()
            .map(GrantedAuthority::getAuthority)
            .collect(java.util.stream.Collectors.toSet());

        assertEquals(Set.of("ROLE_admin", "ROLE_manager"), mappedValues);
    }

    @Test
    void userAuthoritiesMapperForKeycloak_shouldReturnEmpty_whenRealmAccessMissing() {
        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        OAuth2UserAuthority authority = new OAuth2UserAuthority(Map.of("preferred_username", "alice"));

        var mapped = mapper.mapAuthorities(List.of(authority));

        assertTrue(mapped.isEmpty());
    }

    @Test
    void userAuthoritiesMapperForKeycloak_shouldThrowException_whenRolesTypeIsInvalid() {
        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        OAuth2UserAuthority authority = new OAuth2UserAuthority(Map.of(
            "realm_access", Map.of("roles", "admin")
        ));

        assertThrows(ClassCastException.class, () -> mapper.mapAuthorities(List.of(authority)));
    }

    @Test
    void userAuthoritiesMapperForKeycloak_shouldThrowException_whenAuthoritiesCollectionIsEmpty() {
        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();

        assertThrows(java.util.NoSuchElementException.class, () -> mapper.mapAuthorities(List.of()));
    }

    @Test
    void userAuthoritiesMapperForKeycloak_shouldMapNoAuthorities_whenRolesListIsEmpty() {
        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        OAuth2UserAuthority authority = new OAuth2UserAuthority(Map.of(
            "realm_access", Map.of("roles", List.of())
        ));

        var mapped = mapper.mapAuthorities(List.of(authority));

        assertFalse(mapped.iterator().hasNext());
    }
}
