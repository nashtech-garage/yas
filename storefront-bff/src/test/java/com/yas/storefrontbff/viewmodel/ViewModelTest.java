package com.yas.storefrontbff.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ViewModelTest {

    @Test
    void tokenResponseVm_ShouldStoreAccessAndRefreshToken() {
        TokenResponseVm vm = new TokenResponseVm("access-token", "refresh-token");

        assertThat(vm.accessToken()).isEqualTo("access-token");
        assertThat(vm.refreshToken()).isEqualTo("refresh-token");
    }

    @Test
    void guestUserVm_ShouldStoreGuestCredentials() {
        GuestUserVm vm = new GuestUserVm("guest-1", "guest@example.com", "secret");

        assertThat(vm.userId()).isEqualTo("guest-1");
        assertThat(vm.email()).isEqualTo("guest@example.com");
        assertThat(vm.password()).isEqualTo("secret");
    }

    @Test
    void cartDetailVm_ShouldStoreCartDetailFields() {
        CartDetailVm vm = new CartDetailVm(10L, 99L, 3);

        assertThat(vm.id()).isEqualTo(10L);
        assertThat(vm.productId()).isEqualTo(99L);
        assertThat(vm.quantity()).isEqualTo(3);
    }

    @Test
    void cartItemVmFromCartDetailVm_ShouldMapProductIdAndQuantity() {
        CartDetailVm cartDetailVm = new CartDetailVm(11L, 123L, 2);

        CartItemVm vm = CartItemVm.fromCartDetailVm(cartDetailVm);

        assertThat(vm.productId()).isEqualTo(123L);
        assertThat(vm.quantity()).isEqualTo(2);
    }

    @Test
    void cartGetDetailVm_ShouldStoreCartAndDetails() {
        CartDetailVm detail1 = new CartDetailVm(1L, 101L, 1);
        CartDetailVm detail2 = new CartDetailVm(2L, 102L, 4);

        CartGetDetailVm vm = new CartGetDetailVm(1000L, "customer-1", List.of(detail1, detail2));

        assertThat(vm.id()).isEqualTo(1000L);
        assertThat(vm.customerId()).isEqualTo("customer-1");
        assertThat(vm.cartDetails()).containsExactly(detail1, detail2);
    }
}
