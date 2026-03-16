package com.yas.backofficebff.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(clientRegistrationRepository);
    }

    // -----------------------------------------------------------------------
    // generateAuthoritiesFromClaim
    // -----------------------------------------------------------------------

    @Test
    void generateAuthoritiesFromClaim_shouldPrefixRolesWithROLE_() {
        Collection<String> roles = List.of("ADMIN", "USER");

        Collection<GrantedAuthority> authorities = securityConfig.generateAuthoritiesFromClaim(roles);

        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void generateAuthoritiesFromClaim_shouldReturnEmptyCollectionForEmptyRoles() {
        Collection<GrantedAuthority> authorities = securityConfig.generateAuthoritiesFromClaim(Collections.emptyList());
        assertThat(authorities).isEmpty();
    }

    @Test
    void generateAuthoritiesFromClaim_shouldCreateSimpleGrantedAuthority() {
        Collection<GrantedAuthority> authorities = securityConfig.generateAuthoritiesFromClaim(List.of("ADMIN"));

        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next()).isInstanceOf(SimpleGrantedAuthority.class);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void generateAuthoritiesFromClaim_shouldHandleSingleRole() {
        Collection<GrantedAuthority> authorities = securityConfig.generateAuthoritiesFromClaim(List.of("MANAGER"));
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_MANAGER");
    }

    // -----------------------------------------------------------------------
    // userAuthoritiesMapperForKeycloak — OIDC path
    // -----------------------------------------------------------------------

    @Test
    void authoritiesMapper_shouldMapRolesFromOidcUserInfoRealmAccessClaim() {
        Map<String, Object> realmAccess = Map.of("roles", List.of("ADMIN", "USER"));
        Map<String, Object> claims = new HashMap<>();
        claims.put("realm_access", realmAccess);
        claims.put("sub", "user-id");
        claims.put("preferred_username", "john");

        OidcUserInfo userInfo = new OidcUserInfo(claims);
        OidcIdToken idToken = new OidcIdToken(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("sub", "user-id")
        );

        OidcUserAuthority oidcUserAuthority = new OidcUserAuthority(idToken, userInfo);
        Collection<GrantedAuthority> inputAuthorities = List.of(oidcUserAuthority);

        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        Collection<? extends GrantedAuthority> mappedAuthorities = mapper.mapAuthorities(inputAuthorities);

        assertThat(mappedAuthorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void authoritiesMapper_shouldReturnEmptyWhenOidcUserInfoHasNoRealmAccessClaim() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user-id");
        claims.put("preferred_username", "john");
        // no realm_access claim

        OidcUserInfo userInfo = new OidcUserInfo(claims);
        OidcIdToken idToken = new OidcIdToken(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("sub", "user-id")
        );

        OidcUserAuthority oidcUserAuthority = new OidcUserAuthority(idToken, userInfo);

        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        Collection<? extends GrantedAuthority> mappedAuthorities = mapper.mapAuthorities(List.of(oidcUserAuthority));

        assertThat(mappedAuthorities).isEmpty();
    }

    // -----------------------------------------------------------------------
    // userAuthoritiesMapperForKeycloak — OAuth2 (non-OIDC) path
    // -----------------------------------------------------------------------

    @Test
    void authoritiesMapper_shouldMapRolesFromOAuth2UserAttributesRealmAccessClaim() {
        Map<String, Object> realmAccess = Map.of("roles", List.of("ADMIN"));
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("realm_access", realmAccess);
        userAttributes.put("sub", "user-id");

        OAuth2UserAuthority oauth2UserAuthority = new OAuth2UserAuthority(userAttributes);

        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        Collection<? extends GrantedAuthority> mappedAuthorities = mapper.mapAuthorities(List.of(oauth2UserAuthority));

        assertThat(mappedAuthorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADMIN");
    }

    @Test
    void authoritiesMapper_shouldReturnEmptyWhenOAuth2UserAttributesHaveNoRealmAccessClaim() {
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("sub", "user-id");
        // no realm_access

        OAuth2UserAuthority oauth2UserAuthority = new OAuth2UserAuthority(userAttributes);

        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        Collection<? extends GrantedAuthority> mappedAuthorities = mapper.mapAuthorities(List.of(oauth2UserAuthority));

        assertThat(mappedAuthorities).isEmpty();
    }

    @Test
    void authoritiesMapper_shouldMapMultipleRolesFromOAuth2UserAttributes() {
        Map<String, Object> realmAccess = Map.of("roles", List.of("ADMIN", "MANAGER", "USER"));
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("realm_access", realmAccess);
        userAttributes.put("sub", "user-id");

        OAuth2UserAuthority oauth2UserAuthority = new OAuth2UserAuthority(userAttributes);

        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        Collection<? extends GrantedAuthority> mappedAuthorities = mapper.mapAuthorities(List.of(oauth2UserAuthority));

        assertThat(mappedAuthorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_MANAGER", "ROLE_USER");
    }
}