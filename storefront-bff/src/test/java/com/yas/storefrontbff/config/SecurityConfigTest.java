package com.yas.storefrontbff.config;

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
        Collection<GrantedAuthority> authorities =
                securityConfig.generateAuthoritiesFromClaim(List.of("ADMIN", "USER"));

        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void generateAuthoritiesFromClaim_shouldReturnEmptyForEmptyRoles() {
        Collection<GrantedAuthority> authorities =
                securityConfig.generateAuthoritiesFromClaim(Collections.emptyList());

        assertThat(authorities).isEmpty();
    }

    @Test
    void generateAuthoritiesFromClaim_shouldCreateSimpleGrantedAuthority() {
        Collection<GrantedAuthority> authorities =
                securityConfig.generateAuthoritiesFromClaim(List.of("CUSTOMER"));

        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next()).isInstanceOf(SimpleGrantedAuthority.class);
    }

    @Test
    void generateAuthoritiesFromClaim_shouldHandleSingleRole() {
        Collection<GrantedAuthority> authorities =
                securityConfig.generateAuthoritiesFromClaim(List.of("MANAGER"));

        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_MANAGER");
    }

    @Test
    void generateAuthoritiesFromClaim_shouldHandleManyRoles() {
        List<String> roles = List.of("ADMIN", "USER", "CUSTOMER", "GUEST");
        Collection<GrantedAuthority> authorities = securityConfig.generateAuthoritiesFromClaim(roles);

        assertThat(authorities).hasSize(4);
        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER", "ROLE_CUSTOMER", "ROLE_GUEST");
    }

    // -----------------------------------------------------------------------
    // userAuthoritiesMapperForKeycloak — OIDC path
    // -----------------------------------------------------------------------

    @Test
    void authoritiesMapper_oidcPath_shouldMapRolesFromUserInfoRealmAccessClaim() {
        OidcUserAuthority oidcUserAuthority = buildOidcAuthority(
                Map.of("realm_access", Map.of("roles", List.of("ADMIN", "USER")))
        );

        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        Collection<? extends GrantedAuthority> result = mapper.mapAuthorities(List.of(oidcUserAuthority));

        assertThat(result)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void authoritiesMapper_oidcPath_shouldReturnEmptyWhenNoRealmAccessClaim() {
        OidcUserAuthority oidcUserAuthority = buildOidcAuthority(Map.of("sub", "user-id"));

        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        Collection<? extends GrantedAuthority> result = mapper.mapAuthorities(List.of(oidcUserAuthority));

        assertThat(result).isEmpty();
    }

    @Test
    void authoritiesMapper_oidcPath_shouldMapSingleRole() {
        OidcUserAuthority oidcUserAuthority = buildOidcAuthority(
                Map.of("realm_access", Map.of("roles", List.of("CUSTOMER")))
        );

        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        Collection<? extends GrantedAuthority> result = mapper.mapAuthorities(List.of(oidcUserAuthority));

        assertThat(result)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_CUSTOMER");
    }

    // -----------------------------------------------------------------------
    // userAuthoritiesMapperForKeycloak — OAuth2 (non-OIDC) path
    // -----------------------------------------------------------------------

    @Test
    void authoritiesMapper_oauth2Path_shouldMapRolesFromAttributesRealmAccessClaim() {
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("realm_access", Map.of("roles", List.of("ADMIN")));
        userAttributes.put("sub", "user-id");

        OAuth2UserAuthority oauth2UserAuthority = new OAuth2UserAuthority(userAttributes);

        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        Collection<? extends GrantedAuthority> result = mapper.mapAuthorities(List.of(oauth2UserAuthority));

        assertThat(result)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADMIN");
    }

    @Test
    void authoritiesMapper_oauth2Path_shouldReturnEmptyWhenNoRealmAccessClaim() {
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("sub", "user-id");

        OAuth2UserAuthority oauth2UserAuthority = new OAuth2UserAuthority(userAttributes);

        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        Collection<? extends GrantedAuthority> result = mapper.mapAuthorities(List.of(oauth2UserAuthority));

        assertThat(result).isEmpty();
    }

    @Test
    void authoritiesMapper_oauth2Path_shouldMapMultipleRoles() {
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("realm_access", Map.of("roles", List.of("ADMIN", "CUSTOMER", "GUEST")));
        userAttributes.put("sub", "user-id");

        OAuth2UserAuthority oauth2UserAuthority = new OAuth2UserAuthority(userAttributes);

        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        Collection<? extends GrantedAuthority> result = mapper.mapAuthorities(List.of(oauth2UserAuthority));

        assertThat(result)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_GUEST");
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private OidcUserAuthority buildOidcAuthority(Map<String, Object> userInfoClaims) {
        Map<String, Object> allClaims = new HashMap<>(userInfoClaims);
        allClaims.putIfAbsent("sub", "user-id");
        allClaims.putIfAbsent("preferred_username", "test-user");

        OidcUserInfo userInfo = new OidcUserInfo(allClaims);
        OidcIdToken idToken = new OidcIdToken(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("sub", "user-id")
        );
        return new OidcUserAuthority(idToken, userInfo);
    }
}