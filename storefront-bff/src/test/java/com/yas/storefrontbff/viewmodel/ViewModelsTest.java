package com.yas.storefrontbff.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class ViewModelsTest {

    @Test
    void testCartItemVm() {
        CartItemVm item = new CartItemVm(1L, 1);
        assertEquals(1L, item.productId());
        assertEquals(1, item.quantity());
        
        CartDetailVm detail = new CartDetailVm(1L, 1L, 1);
        CartItemVm fromDetail = CartItemVm.fromCartDetailVm(detail);
        assertEquals(1L, fromDetail.productId());
        assertEquals(1, fromDetail.quantity());
    }

    @Test
    void testTokenResponseVm() {
        TokenResponseVm vm = new TokenResponseVm("access", "refresh");
        assertEquals("access", vm.accessToken());
        assertEquals("refresh", vm.refreshToken());
    }

    @Test
    void testAuthenticationInfoVm() {
        AuthenticatedUserVm user = new AuthenticatedUserVm("user");
        AuthenticationInfoVm vm = new AuthenticationInfoVm(true, user);
        assertEquals(true, vm.isAuthenticated());
        assertEquals(user, vm.authenticatedUser());
    }

    @Test
    void testCartGetDetailVm() {
        CartGetDetailVm vm = new CartGetDetailVm(1L, "cus1", List.of());
        assertEquals(1L, vm.id());
        assertEquals("cus1", vm.customerId());
        assertEquals(0, vm.cartDetails().size());
    }
}
