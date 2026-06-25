package com.yas.storefrontbff.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    private SecurityConfig securityConfig;
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @BeforeEach
    void setUp() {
        clientRegistrationRepository = mock(ReactiveClientRegistrationRepository.class);
        securityConfig = new SecurityConfig(clientRegistrationRepository);
    }

    @Test
    void testGenerateAuthoritiesFromClaim() {
        Collection<GrantedAuthority> authorities = securityConfig.generateAuthoritiesFromClaim(List.of("ADMIN", "USER"));
        assertThat(authorities).hasSize(2);
        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void testUserAuthoritiesMapperForKeycloak_withOidcUserAuthority() {
        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        
        Map<String, Object> realmAccess = Map.of("roles", List.of("ADMIN"));
        Map<String, Object> claims = Map.of("realm_access", realmAccess);
        
        OidcIdToken idToken = new OidcIdToken("tokenValue", null, null, Map.of("sub", "user1"));
        OidcUserInfo userInfo = new OidcUserInfo(claims);
        OidcUserAuthority authority = new OidcUserAuthority(idToken, userInfo);

        Collection<? extends GrantedAuthority> mappedAuthorities = mapper.mapAuthorities(Set.of(authority));
        
        assertThat(mappedAuthorities).hasSize(1);
        assertThat(mappedAuthorities.iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void testUserAuthoritiesMapperForKeycloak_withOAuth2UserAuthority() {
        GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();
        
        Map<String, Object> realmAccess = Map.of("roles", List.of("USER"));
        Map<String, Object> attributes = Map.of("realm_access", realmAccess);
        
        OAuth2UserAuthority authority = new OAuth2UserAuthority(attributes);

        Collection<? extends GrantedAuthority> mappedAuthorities = mapper.mapAuthorities(Set.of(authority));
        
        assertThat(mappedAuthorities).hasSize(1);
        assertThat(mappedAuthorities.iterator().next().getAuthority()).isEqualTo("ROLE_USER");
    }
}

