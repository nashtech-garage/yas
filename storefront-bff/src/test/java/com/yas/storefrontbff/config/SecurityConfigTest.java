package com.yas.storefrontbff.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        ReactiveClientRegistrationRepository repository = Mockito.mock(ReactiveClientRegistrationRepository.class);
        securityConfig = new SecurityConfig(repository);
    }

    @Test
    void userAuthoritiesMapperForKeycloak_WhenOidcAuthorityContainsRoles_ShouldMapRoles() {
        OidcIdToken idToken = new OidcIdToken(
            "token-value",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            Map.of("sub", "user-1")
        );
        OidcUserInfo userInfo = new OidcUserInfo(Map.of("realm_access", Map.of("roles", List.of("admin", "user"))));
        OidcUserAuthority oidcAuthority = new OidcUserAuthority(idToken, userInfo);

        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        Set<String> mapped = mapper.mapAuthorities(Set.of(oidcAuthority))
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());

        assertThat(mapped).containsExactlyInAnyOrder("ROLE_admin", "ROLE_user");
    }

    @Test
    void userAuthoritiesMapperForKeycloak_WhenOauth2AuthorityContainsRoles_ShouldMapRoles() {
        OAuth2UserAuthority oauth2Authority = new OAuth2UserAuthority(
            Map.of("realm_access", Map.of("roles", List.of("manager")))
        );

        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        Set<String> mapped = mapper.mapAuthorities(Set.of(oauth2Authority))
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());

        assertThat(mapped).containsExactly("ROLE_manager");
    }

    @Test
    void userAuthoritiesMapperForKeycloak_WhenClaimIsMissing_ShouldReturnEmptyAuthorities() {
        OAuth2UserAuthority oauth2Authority = new OAuth2UserAuthority(Map.of("sub", "user-1"));

        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        var mapped = mapper.mapAuthorities(Set.of(oauth2Authority));

        assertThat(mapped).isEmpty();
    }

    @Test
    void generateAuthoritiesFromClaim_ShouldPrefixRole() {
        var roles = securityConfig.generateAuthoritiesFromClaim(List.of("seller", "editor"));

        assertThat(roles)
            .extracting(GrantedAuthority::getAuthority)
            .containsExactlyInAnyOrder("ROLE_seller", "ROLE_editor");
    }
}
