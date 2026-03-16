package com.yas.storefrontbff.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.junit.jupiter.api.Test;

class ViewModelTest {

    @Test
    void authenticatedUserVm_HoldsUsername() {
        AuthenticatedUserVm vm = new AuthenticatedUserVm("alice");

        assertEquals("alice", vm.username());
    }

    @Test
    void authenticationInfoVm_HoldsAuthenticationData() {
        AuthenticationInfoVm vm = new AuthenticationInfoVm(true, new AuthenticatedUserVm("bob"));

        assertEquals(true, vm.isAuthenticated());
        assertNotNull(vm.authenticatedUser());
        assertEquals("bob", vm.authenticatedUser().username());
    }

    @Test
    void cartItemVm_FromCartDetailVm_MapsProductAndQuantity() {
        CartDetailVm detailVm = new CartDetailVm(1L, 101L, 3);

        CartItemVm itemVm = CartItemVm.fromCartDetailVm(detailVm);

        assertEquals(101L, itemVm.productId());
        assertEquals(3, itemVm.quantity());
    }

    @Test
    void cartGetDetailVm_HoldsCartDetails() {
        CartDetailVm detailVm = new CartDetailVm(10L, 200L, 2);
        CartGetDetailVm vm = new CartGetDetailVm(7L, "customer-1", List.of(detailVm));

        assertEquals(7L, vm.id());
        assertEquals("customer-1", vm.customerId());
        assertEquals(1, vm.cartDetails().size());
        assertEquals(200L, vm.cartDetails().getFirst().productId());
    }

    @Test
    void guestUserVm_HoldsGuestCredentials() {
        GuestUserVm vm = new GuestUserVm("guest-id", "guest@yas.local", "pass");

        assertEquals("guest-id", vm.userId());
        assertEquals("guest@yas.local", vm.email());
        assertEquals("pass", vm.password());
    }

    @Test
    void tokenResponseVm_HoldsTokens() {
        TokenResponseVm vm = new TokenResponseVm("access", "refresh");

        assertEquals("access", vm.accessToken());
        assertEquals("refresh", vm.refreshToken());
    }
}
