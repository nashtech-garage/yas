package com.yas.storefrontbff.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

class SecurityConfigTest {

    private final ReactiveClientRegistrationRepository clientRegistrationRepository =
            mock(ReactiveClientRegistrationRepository.class);
    private final SecurityConfig securityConfig = new SecurityConfig(clientRegistrationRepository);

    @Test
    void generateAuthoritiesFromClaim_MapsRolesWithRolePrefix() {
        var result = securityConfig.generateAuthoritiesFromClaim(List.of("admin", "user"));
        Set<String> authorities = result.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

        assertEquals(Set.of("ROLE_admin", "ROLE_user"), authorities);
    }

    @Test
    void userAuthoritiesMapperForKeycloak_WhenOidcClaimExists_ReturnsMappedAuthorities() {
        OidcUserAuthority oidcUserAuthority = mock(OidcUserAuthority.class);
        OidcUserInfo userInfo = mock(OidcUserInfo.class);

        when(oidcUserAuthority.getUserInfo()).thenReturn(userInfo);
        when(userInfo.hasClaim("realm_access")).thenReturn(true);
        when(userInfo.getClaimAsMap("realm_access")).thenReturn(Map.of("roles", List.of("customer", "vip")));

        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        Set<String> authorities = mapper.mapAuthorities(Set.of(oidcUserAuthority)).stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertEquals(Set.of("ROLE_customer", "ROLE_vip"), authorities);
    }

    @Test
    void userAuthoritiesMapperForKeycloak_WhenOauth2ClaimExists_ReturnsMappedAuthorities() {
        OAuth2UserAuthority oauth2UserAuthority = mock(OAuth2UserAuthority.class);

        when(oauth2UserAuthority.getAttributes())
                .thenReturn(Map.of("realm_access", Map.of("roles", List.of("buyer"))));

        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        Set<String> authorities = mapper.mapAuthorities(Set.of(oauth2UserAuthority)).stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertEquals(Set.of("ROLE_buyer"), authorities);
    }

    @Test
    void userAuthoritiesMapperForKeycloak_WhenNoClaim_ReturnsEmptyAuthorities() {
        OAuth2UserAuthority oauth2UserAuthority = mock(OAuth2UserAuthority.class);
        when(oauth2UserAuthority.getAttributes()).thenReturn(Map.of("sub", "user-1"));

        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        var result = mapper.mapAuthorities(Set.of(oauth2UserAuthority));

        assertTrue(result.isEmpty());
    }

}
