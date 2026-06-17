package com.yas.storefrontbff.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        ReactiveClientRegistrationRepository repo = mock(ReactiveClientRegistrationRepository.class);
        securityConfig = new SecurityConfig(repo);
    }

    // ═══════════════════════════════════════════════════════════════
    // generateAuthoritiesFromClaim
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("generateAuthoritiesFromClaim")
    class GenerateAuthoritiesFromClaim {

        @Test
        @DisplayName("roles hợp lệ → prefix ROLE_ được thêm vào mỗi role")
        void whenRolesProvided_shouldPrefixWithRole() {
            Collection<GrantedAuthority> authorities =
                securityConfig.generateAuthoritiesFromClaim(List.of("admin", "user", "manager"));

            assertThat(authorities).hasSize(3);
            assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_admin", "ROLE_user", "ROLE_manager");
        }

        @Test
        @DisplayName("roles rỗng → trả về collection rỗng")
        void whenRolesEmpty_shouldReturnEmptyCollection() {
            Collection<GrantedAuthority> authorities =
                securityConfig.generateAuthoritiesFromClaim(List.of());

            assertThat(authorities).isEmpty();
        }

        @Test
        @DisplayName("role đã có prefix ROLE_ → vẫn thêm ROLE_ thêm lần nữa")
        void whenRoleAlreadyHasPrefix_shouldStillAddPrefix() {
            Collection<GrantedAuthority> authorities =
                securityConfig.generateAuthoritiesFromClaim(List.of("ROLE_admin"));

            assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ROLE_admin");
        }

        @Test
        @DisplayName("single role → trả về 1 authority")
        void whenSingleRole_shouldReturnOneAuthority() {
            Collection<GrantedAuthority> authorities =
                securityConfig.generateAuthoritiesFromClaim(List.of("viewer"));

            assertThat(authorities).hasSize(1);
            assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_viewer");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // userAuthoritiesMapperForKeycloak — OIDC path
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("userAuthoritiesMapperForKeycloak — OIDC")
    class AuthoritiesMapperOidc {

        @Test
        @DisplayName("OIDC user có realm_access → roles được map đúng")
        void whenOidcUserHasRealmAccess_shouldMapRoles() {
            GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();

            OidcIdToken idToken = OidcIdToken.withTokenValue("token")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .subject("user-123")
                .build();

            OidcUserInfo userInfo = new OidcUserInfo(Map.of(
                "realm_access", Map.of("roles", List.of("admin", "user")),
                "sub", "user-123"
            ));

            OidcUserAuthority oidcAuthority = new OidcUserAuthority(idToken, userInfo);

            Collection<? extends GrantedAuthority> result = mapper.mapAuthorities(Set.of(oidcAuthority));

            assertThat(result)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_admin", "ROLE_user");
        }

        @Test
        @DisplayName("OIDC user KHÔNG có realm_access → trả về empty set")
        void whenOidcUserHasNoRealmAccess_shouldReturnEmpty() {
            GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();

            OidcIdToken idToken = OidcIdToken.withTokenValue("token")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .subject("user-123")
                .build();

            OidcUserInfo userInfo = new OidcUserInfo(Map.of("sub", "user-123"));

            OidcUserAuthority oidcAuthority = new OidcUserAuthority(idToken, userInfo);

            Collection<? extends GrantedAuthority> result = mapper.mapAuthorities(Set.of(oidcAuthority));

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("OIDC user có realm_access với single role → map đúng")
        void whenOidcUserHasSingleRole_shouldMapCorrectly() {
            GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();

            OidcIdToken idToken = OidcIdToken.withTokenValue("token")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .subject("user-123")
                .build();

            OidcUserInfo userInfo = new OidcUserInfo(Map.of(
                "realm_access", Map.of("roles", List.of("superadmin")),
                "sub", "user-123"
            ));

            OidcUserAuthority oidcAuthority = new OidcUserAuthority(idToken, userInfo);

            Collection<? extends GrantedAuthority> result = mapper.mapAuthorities(Set.of(oidcAuthority));

            assertThat(result)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_superadmin");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // userAuthoritiesMapperForKeycloak — OAuth2 path
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("userAuthoritiesMapperForKeycloak — OAuth2")
    class AuthoritiesMapperOAuth2 {

        @Test
        @DisplayName("OAuth2 user có realm_access → roles được map đúng")
        void whenOAuth2UserHasRealmAccess_shouldMapRoles() {
            GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();

            Map<String, Object> attributes = Map.of(
                "realm_access", Map.of("roles", List.of("editor", "viewer")),
                "sub", "user-456"
            );

            OAuth2UserAuthority oauth2Authority = new OAuth2UserAuthority(attributes);

            Collection<? extends GrantedAuthority> result = mapper.mapAuthorities(Set.of(oauth2Authority));

            assertThat(result)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_editor", "ROLE_viewer");
        }

        @Test
        @DisplayName("OAuth2 user KHÔNG có realm_access → trả về empty set")
        void whenOAuth2UserHasNoRealmAccess_shouldReturnEmpty() {
            GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();

            Map<String, Object> attributes = Map.of("sub", "user-456");
            OAuth2UserAuthority oauth2Authority = new OAuth2UserAuthority(attributes);

            Collection<? extends GrantedAuthority> result = mapper.mapAuthorities(Set.of(oauth2Authority));

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("OAuth2 user có realm_access với nhiều roles → tất cả được map")
        void whenOAuth2UserHasManyRoles_shouldMapAll() {
            GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();

            Map<String, Object> attributes = Map.of(
                "realm_access", Map.of(
                    "roles", List.of("admin", "user", "manager", "viewer", "editor")
                )
            );

            OAuth2UserAuthority oauth2Authority = new OAuth2UserAuthority(attributes);

            Collection<? extends GrantedAuthority> result = mapper.mapAuthorities(Set.of(oauth2Authority));

            assertThat(result).hasSize(5);
            assertThat(result)
                .extracting(GrantedAuthority::getAuthority)
                .allMatch(auth -> auth.startsWith("ROLE_"));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // userAuthoritiesMapperForKeycloak — Unknown authority type (branch coverage)
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("userAuthoritiesMapperForKeycloak — Unknown authority type")
    class AuthoritiesMapperUnknown {

        @Test
        @DisplayName("Authority không phải OIDC hoặc OAuth2 → trả về empty set")
        void whenUnknownAuthorityType_shouldReturnEmpty() {
            GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();

            // SimpleGrantedAuthority là loại khác, không phải OidcUserAuthority / OAuth2UserAuthority
            SimpleGrantedAuthority unknownAuthority = new SimpleGrantedAuthority("ROLE_UNKNOWN");

            Collection<? extends GrantedAuthority> result = mapper.mapAuthorities(Set.of(unknownAuthority));

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Empty authorities set → trả về empty set")
        void whenEmptyAuthoritiesSet_shouldReturnEmpty() {
            GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();

            Collection<? extends GrantedAuthority> result = mapper.mapAuthorities(Set.of());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Mixed: OIDC và Unknown authority → chỉ map OIDC roles")
        void whenMixedOidcAndUnknown_shouldOnlyMapOidcRoles() {
            GrantedAuthoritiesMapper mapper = securityConfig.userAuthoritiesMapperForKeycloak();

            OidcIdToken idToken = OidcIdToken.withTokenValue("token")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .subject("user-123")
                .build();

            OidcUserInfo userInfo = new OidcUserInfo(Map.of(
                "realm_access", Map.of("roles", List.of("admin")),
                "sub", "user-123"
            ));

            OidcUserAuthority oidcAuthority = new OidcUserAuthority(idToken, userInfo);
            SimpleGrantedAuthority unknownAuthority = new SimpleGrantedAuthority("ROLE_OTHER");

            Collection<? extends GrantedAuthority> result =
                mapper.mapAuthorities(Set.of(oidcAuthority, unknownAuthority));

            assertThat(result)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_admin");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // CartItemVm.fromCartDetailVm
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("CartItemVm.fromCartDetailVm")
    class CartItemVmMapping {

        @Test
        @DisplayName("fromCartDetailVm → map đúng productId và quantity")
        void whenCartDetailVm_shouldMapCorrectly() {
            com.yas.storefrontbff.viewmodel.CartDetailVm cartDetailVm =
                new com.yas.storefrontbff.viewmodel.CartDetailVm(1L, 101L, 3);

            com.yas.storefrontbff.viewmodel.CartItemVm cartItemVm =
                com.yas.storefrontbff.viewmodel.CartItemVm.fromCartDetailVm(cartDetailVm);

            assertThat(cartItemVm.productId()).isEqualTo(101L);
            assertThat(cartItemVm.quantity()).isEqualTo(3);
        }

        @Test
        @DisplayName("fromCartDetailVm với quantity = 0 → map đúng")
        void whenQuantityZero_shouldMapCorrectly() {
            com.yas.storefrontbff.viewmodel.CartDetailVm cartDetailVm =
                new com.yas.storefrontbff.viewmodel.CartDetailVm(2L, 202L, 0);

            com.yas.storefrontbff.viewmodel.CartItemVm cartItemVm =
                com.yas.storefrontbff.viewmodel.CartItemVm.fromCartDetailVm(cartDetailVm);

            assertThat(cartItemVm.productId()).isEqualTo(202L);
            assertThat(cartItemVm.quantity()).isEqualTo(0);
        }

        @Test
        @DisplayName("fromCartDetailVm với quantity lớn → map đúng")
        void whenLargeQuantity_shouldMapCorrectly() {
            com.yas.storefrontbff.viewmodel.CartDetailVm cartDetailVm =
                new com.yas.storefrontbff.viewmodel.CartDetailVm(3L, 303L, Integer.MAX_VALUE);

            com.yas.storefrontbff.viewmodel.CartItemVm cartItemVm =
                com.yas.storefrontbff.viewmodel.CartItemVm.fromCartDetailVm(cartDetailVm);

            assertThat(cartItemVm.productId()).isEqualTo(303L);
            assertThat(cartItemVm.quantity()).isEqualTo(Integer.MAX_VALUE);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // ViewModel records
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("ViewModel records")
    class ViewModelRecords {

        @Test
        @DisplayName("AuthenticationInfoVm — authenticated=true, user có username")
        void authenticationInfoVm_authenticated() {
            com.yas.storefrontbff.viewmodel.AuthenticatedUserVm userVm =
                new com.yas.storefrontbff.viewmodel.AuthenticatedUserVm("john");
            com.yas.storefrontbff.viewmodel.AuthenticationInfoVm infoVm =
                new com.yas.storefrontbff.viewmodel.AuthenticationInfoVm(true, userVm);

            assertThat(infoVm.isAuthenticated()).isTrue();
            assertThat(infoVm.authenticatedUser().username()).isEqualTo("john");
        }

        @Test
        @DisplayName("AuthenticationInfoVm — authenticated=false, user=null")
        void authenticationInfoVm_unauthenticated() {
            com.yas.storefrontbff.viewmodel.AuthenticationInfoVm infoVm =
                new com.yas.storefrontbff.viewmodel.AuthenticationInfoVm(false, null);

            assertThat(infoVm.isAuthenticated()).isFalse();
            assertThat(infoVm.authenticatedUser()).isNull();
        }

        @Test
        @DisplayName("TokenResponseVm — accessToken và refreshToken đúng")
        void tokenResponseVm() {
            com.yas.storefrontbff.viewmodel.TokenResponseVm tokenVm =
                new com.yas.storefrontbff.viewmodel.TokenResponseVm("access-123", "refresh-456");

            assertThat(tokenVm.accessToken()).isEqualTo("access-123");
            assertThat(tokenVm.refreshToken()).isEqualTo("refresh-456");
        }

        @Test
        @DisplayName("TokenResponseVm — accessToken null, refreshToken null")
        void tokenResponseVm_nullValues() {
            com.yas.storefrontbff.viewmodel.TokenResponseVm tokenVm =
                new com.yas.storefrontbff.viewmodel.TokenResponseVm(null, null);

            assertThat(tokenVm.accessToken()).isNull();
            assertThat(tokenVm.refreshToken()).isNull();
        }

        @Test
        @DisplayName("GuestUserVm — userId, email, password đúng")
        void guestUserVm() {
            com.yas.storefrontbff.viewmodel.GuestUserVm guestVm =
                new com.yas.storefrontbff.viewmodel.GuestUserVm("guest-001", "guest@yas.com", "pass123");

            assertThat(guestVm.userId()).isEqualTo("guest-001");
            assertThat(guestVm.email()).isEqualTo("guest@yas.com");
            assertThat(guestVm.password()).isEqualTo("pass123");
        }

        @Test
        @DisplayName("CartDetailVm — id, productId, quantity đúng")
        void cartDetailVm() {
            com.yas.storefrontbff.viewmodel.CartDetailVm vm =
                new com.yas.storefrontbff.viewmodel.CartDetailVm(1L, 99L, 5);

            assertThat(vm.id()).isEqualTo(1L);
            assertThat(vm.productId()).isEqualTo(99L);
            assertThat(vm.quantity()).isEqualTo(5);
        }

        @Test
        @DisplayName("CartGetDetailVm — id, customerId, cartDetails đúng")
        void cartGetDetailVm() {
            com.yas.storefrontbff.viewmodel.CartDetailVm detail =
                new com.yas.storefrontbff.viewmodel.CartDetailVm(1L, 101L, 2);

            com.yas.storefrontbff.viewmodel.CartGetDetailVm vm =
                new com.yas.storefrontbff.viewmodel.CartGetDetailVm(10L, "customer-abc", List.of(detail));

            assertThat(vm.id()).isEqualTo(10L);
            assertThat(vm.customerId()).isEqualTo("customer-abc");
            assertThat(vm.cartDetails()).hasSize(1);
            assertThat(vm.cartDetails().get(0).productId()).isEqualTo(101L);
        }

        @Test
        @DisplayName("CartGetDetailVm — cartDetails rỗng")
        void cartGetDetailVm_emptyDetails() {
            com.yas.storefrontbff.viewmodel.CartGetDetailVm vm =
                new com.yas.storefrontbff.viewmodel.CartGetDetailVm(11L, "customer-xyz", List.of());

            assertThat(vm.cartDetails()).isEmpty();
        }

        @Test
        @DisplayName("AuthenticatedUserVm — username null vẫn hợp lệ")
        void authenticatedUserVm_nullUsername() {
            com.yas.storefrontbff.viewmodel.AuthenticatedUserVm userVm =
                new com.yas.storefrontbff.viewmodel.AuthenticatedUserVm(null);

            assertThat(userVm.username()).isNull();
        }
        @Test
        @DisplayName("ServiceUrlConfig services map is exposed")
        void serviceUrlConfig() {
            ServiceUrlConfig config = new ServiceUrlConfig(Map.of(
                "cart", "http://cart-service",
                "product", "http://product-service"
            ));

            assertThat(config.services())
                .containsEntry("cart", "http://cart-service")
                .containsEntry("product", "http://product-service");
        }
    }
}