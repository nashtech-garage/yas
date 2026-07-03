package com.yas.storefrontbff.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

class ViewModelTest {

    @Test
    void records_and_helpers_workAsExpected() {
        CartDetailVm cartDetail = new CartDetailVm(1L, 10L, 2);
        assertThat(cartDetail.id()).isEqualTo(1L);
        assertThat(cartDetail.productId()).isEqualTo(10L);
        assertThat(cartDetail.quantity()).isEqualTo(2);

        CartItemVm cartItem = CartItemVm.fromCartDetailVm(cartDetail);
        assertThat(cartItem.productId()).isEqualTo(10L);
        assertThat(cartItem.quantity()).isEqualTo(2);

        CartGetDetailVm cart = new CartGetDetailVm(99L, "c-1", List.of(cartDetail));
        assertThat(cart.id()).isEqualTo(99L);
        assertThat(cart.customerId()).isEqualTo("c-1");
        assertThat(cart.cartDetails()).hasSize(1);

        GuestUserVm guest = new GuestUserVm("g-1", "guest@example.com", "pw");
        assertThat(guest.userId()).isEqualTo("g-1");
        assertThat(guest.email()).isEqualTo("guest@example.com");
        assertThat(guest.password()).isEqualTo("pw");

        TokenResponseVm token = new TokenResponseVm("access", "refresh");
        assertThat(token.accessToken()).isEqualTo("access");
        assertThat(token.refreshToken()).isEqualTo("refresh");

        AuthenticatedUserVm user = new AuthenticatedUserVm("alice");
        assertThat(user.username()).isEqualTo("alice");

        AuthenticationInfoVm info = new AuthenticationInfoVm(true, user);
        assertThat(info.isAuthenticated()).isTrue();
        assertThat(info.authenticatedUser()).isSameAs(user);
    }

    @Test
    void cartItem_fromCartDetail_throwsOnNull() {
        assertThatThrownBy(() -> CartItemVm.fromCartDetailVm(null))
            .isInstanceOf(NullPointerException.class);
    }
}
