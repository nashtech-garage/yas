package com.yas.storefrontbff.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.storefrontbff.config.ServiceUrlConfig;
import java.util.List;
import org.junit.jupiter.api.Test;

class ViewModelTest {

    // ---- AuthenticatedUserVm ----
    @Test
    void authenticatedUserVm_shouldStoreUsername() {
        AuthenticatedUserVm vm = new AuthenticatedUserVm("john");
        assertThat(vm.username()).isEqualTo("john");
    }

    @Test
    void authenticatedUserVm_withNullUsername_shouldAllowNull() {
        AuthenticatedUserVm vm = new AuthenticatedUserVm(null);
        assertThat(vm.username()).isNull();
    }

    @Test
    void authenticatedUserVm_equalsSameValue_shouldBeEqual() {
        AuthenticatedUserVm a = new AuthenticatedUserVm("alice");
        AuthenticatedUserVm b = new AuthenticatedUserVm("alice");
        assertThat(a).isEqualTo(b);
    }

    @Test
    void authenticatedUserVm_differentValues_shouldNotBeEqual() {
        AuthenticatedUserVm a = new AuthenticatedUserVm("alice");
        AuthenticatedUserVm b = new AuthenticatedUserVm("bob");
        assertThat(a).isNotEqualTo(b);
    }

    // ---- AuthenticationInfoVm ----
    @Test
    void authenticationInfoVm_whenAuthenticated_shouldReturnTrue() {
        AuthenticatedUserVm user = new AuthenticatedUserVm("alice");
        AuthenticationInfoVm vm = new AuthenticationInfoVm(true, user);
        assertThat(vm.isAuthenticated()).isTrue();
        assertThat(vm.authenticatedUser()).isEqualTo(user);
    }

    @Test
    void authenticationInfoVm_whenNotAuthenticated_shouldReturnFalse() {
        AuthenticationInfoVm vm = new AuthenticationInfoVm(false, null);
        assertThat(vm.isAuthenticated()).isFalse();
        assertThat(vm.authenticatedUser()).isNull();
    }

    @Test
    void authenticationInfoVm_equalsWithSameValues_shouldBeEqual() {
        AuthenticationInfoVm a = new AuthenticationInfoVm(true, new AuthenticatedUserVm("user"));
        AuthenticationInfoVm b = new AuthenticationInfoVm(true, new AuthenticatedUserVm("user"));
        assertThat(a).isEqualTo(b);
    }

    // ---- CartDetailVm ----
    @Test
    void cartDetailVm_shouldStoreAllFields() {
        CartDetailVm vm = new CartDetailVm(1L, 100L, 3);
        assertThat(vm.id()).isEqualTo(1L);
        assertThat(vm.productId()).isEqualTo(100L);
        assertThat(vm.quantity()).isEqualTo(3);
    }

    @Test
    void cartDetailVm_equalsSameValues_shouldBeEqual() {
        CartDetailVm a = new CartDetailVm(1L, 100L, 3);
        CartDetailVm b = new CartDetailVm(1L, 100L, 3);
        assertThat(a).isEqualTo(b);
    }

    // ---- CartItemVm ----
    @Test
    void cartItemVm_fromCartDetailVm_shouldMapCorrectly() {
        CartDetailVm detail = new CartDetailVm(1L, 42L, 5);
        CartItemVm item = CartItemVm.fromCartDetailVm(detail);
        assertThat(item.productId()).isEqualTo(42L);
        assertThat(item.quantity()).isEqualTo(5);
    }

    @Test
    void cartItemVm_directConstruction_shouldStoreFields() {
        CartItemVm vm = new CartItemVm(99L, 2);
        assertThat(vm.productId()).isEqualTo(99L);
        assertThat(vm.quantity()).isEqualTo(2);
    }

    // ---- CartGetDetailVm ----
    @Test
    void cartGetDetailVm_shouldStoreAllFields() {
        List<CartDetailVm> items = List.of(new CartDetailVm(1L, 10L, 2));
        CartGetDetailVm vm = new CartGetDetailVm(5L, "customer-123", items);
        assertThat(vm.id()).isEqualTo(5L);
        assertThat(vm.customerId()).isEqualTo("customer-123");
        assertThat(vm.cartDetails()).hasSize(1);
    }

    // ---- GuestUserVm ----
    @Test
    void guestUserVm_shouldStoreAllFields() {
        GuestUserVm vm = new GuestUserVm("uid-1", "user@test.com", "secret");
        assertThat(vm.userId()).isEqualTo("uid-1");
        assertThat(vm.email()).isEqualTo("user@test.com");
        assertThat(vm.password()).isEqualTo("secret");
    }

    @Test
    void guestUserVm_equalsWithSameValues_shouldBeEqual() {
        GuestUserVm a = new GuestUserVm("id", "email@a.com", "pass");
        GuestUserVm b = new GuestUserVm("id", "email@a.com", "pass");
        assertThat(a).isEqualTo(b);
    }

    // ---- TokenResponseVm ----
    @Test
    void tokenResponseVm_shouldStoreAccessAndRefreshToken() {
        TokenResponseVm vm = new TokenResponseVm("access123", "refresh456");
        assertThat(vm.accessToken()).isEqualTo("access123");
        assertThat(vm.refreshToken()).isEqualTo("refresh456");
    }

    @Test
    void tokenResponseVm_equalsWithSameValues_shouldBeEqual() {
        TokenResponseVm a = new TokenResponseVm("acc", "ref");
        TokenResponseVm b = new TokenResponseVm("acc", "ref");
        assertThat(a).isEqualTo(b);
    }

    @Test
    void tokenResponseVm_toString_shouldContainAccessToken() {
        TokenResponseVm vm = new TokenResponseVm("myToken", "myRefresh");
        assertThat(vm.toString()).contains("myToken");
    }

    // ---- ServiceUrlConfig ----
    @Test
    void serviceUrlConfig_shouldStoreServicesMap() {
        java.util.Map<String, String> services = new java.util.HashMap<>();
        services.put("customer", "http://localhost:8080/customer");
        services.put("cart", "http://localhost:8080/cart");
        ServiceUrlConfig config = new ServiceUrlConfig(services);
        assertThat(config.services()).containsKey("customer");
        assertThat(config.services().get("customer")).isEqualTo("http://localhost:8080/customer");
    }

    @Test
    void serviceUrlConfig_equalsWithSameMap_shouldBeEqual() {
        java.util.Map<String, String> map = java.util.Map.of("k", "v");
        ServiceUrlConfig a = new ServiceUrlConfig(map);
        ServiceUrlConfig b = new ServiceUrlConfig(map);
        assertThat(a).isEqualTo(b);
    }

    @Test
    void serviceUrlConfig_toString_shouldContainServices() {
        java.util.Map<String, String> map = java.util.Map.of("api", "http://api");
        ServiceUrlConfig config = new ServiceUrlConfig(map);
        assertThat(config.toString()).contains("api");
    }
}
