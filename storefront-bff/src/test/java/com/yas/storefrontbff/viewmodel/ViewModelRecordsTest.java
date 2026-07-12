package com.yas.storefrontbff.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class ViewModelRecordsTest {

    @Test
    void cartDetailVm_shouldExposeAllFields_andSupportRecordContracts() {
        CartDetailVm vm = new CartDetailVm(1L, 100L, 2);

        assertEquals(1L, vm.id());
        assertEquals(100L, vm.productId());
        assertEquals(2, vm.quantity());
        assertNotNull(vm.toString());
        assertEquals(vm, new CartDetailVm(1L, 100L, 2));
        assertNotEquals(vm, new CartDetailVm(2L, 100L, 2));
        assertEquals(vm.hashCode(), new CartDetailVm(1L, 100L, 2).hashCode());
    }

    @Test
    void cartItemVm_shouldExposeAllFields_andSupportRecordContracts() {
        CartItemVm vm = new CartItemVm(100L, 3);

        assertEquals(100L, vm.productId());
        assertEquals(3, vm.quantity());
        assertNotNull(vm.toString());
        assertEquals(vm, new CartItemVm(100L, 3));
        assertNotEquals(vm, new CartItemVm(101L, 3));
        assertEquals(vm.hashCode(), new CartItemVm(100L, 3).hashCode());
    }

    @Test
    void fromCartDetailVm_shouldMapValuesCorrectly() {
        CartDetailVm detail = new CartDetailVm(5L, 55L, 7);

        CartItemVm vm = CartItemVm.fromCartDetailVm(detail);

        assertEquals(55L, vm.productId());
        assertEquals(7, vm.quantity());
    }

    @Test
    void fromCartDetailVm_shouldThrowException_whenInputIsNull() {
        assertThrows(NullPointerException.class, () -> CartItemVm.fromCartDetailVm(null));
    }

    @Test
    void cartGetDetailVm_shouldExposeAllFields_andSupportRecordContracts() {
        List<CartDetailVm> details = List.of(new CartDetailVm(1L, 100L, 2));
        CartGetDetailVm vm = new CartGetDetailVm(10L, "customer-1", details);

        assertEquals(10L, vm.id());
        assertEquals("customer-1", vm.customerId());
        assertEquals(details, vm.cartDetails());
        assertNotNull(vm.toString());
        assertEquals(vm, new CartGetDetailVm(10L, "customer-1", details));
        assertNotEquals(vm, new CartGetDetailVm(11L, "customer-1", details));
        assertEquals(vm.hashCode(), new CartGetDetailVm(10L, "customer-1", details).hashCode());
    }

    @Test
    void guestUserVm_shouldExposeAllFields_andSupportRecordContracts() {
        GuestUserVm vm = new GuestUserVm("guest-1", "guest@example.com", "pw");

        assertEquals("guest-1", vm.userId());
        assertEquals("guest@example.com", vm.email());
        assertEquals("pw", vm.password());
        assertNotNull(vm.toString());
        assertEquals(vm, new GuestUserVm("guest-1", "guest@example.com", "pw"));
        assertNotEquals(vm, new GuestUserVm("guest-2", "guest@example.com", "pw"));
        assertEquals(vm.hashCode(), new GuestUserVm("guest-1", "guest@example.com", "pw").hashCode());
    }

    @Test
    void tokenResponseVm_shouldExposeAllFields_andSupportRecordContracts() {
        TokenResponseVm vm = new TokenResponseVm("access-token", "refresh-token");

        assertEquals("access-token", vm.accessToken());
        assertEquals("refresh-token", vm.refreshToken());
        assertNotNull(vm.toString());
        assertEquals(vm, new TokenResponseVm("access-token", "refresh-token"));
        assertNotEquals(vm, new TokenResponseVm("other", "refresh-token"));
        assertEquals(vm.hashCode(), new TokenResponseVm("access-token", "refresh-token").hashCode());
    }

    @Test
    void authenticationInfoVm_andAuthenticatedUserVm_shouldExposeFields_andSupportRecordContracts() {
        AuthenticatedUserVm userVm = new AuthenticatedUserVm("alice");
        AuthenticationInfoVm vm = new AuthenticationInfoVm(true, userVm);

        assertEquals("alice", userVm.username());
        assertEquals(true, vm.isAuthenticated());
        assertEquals(userVm, vm.authenticatedUser());

        assertNotNull(userVm.toString());
        assertEquals(userVm, new AuthenticatedUserVm("alice"));
        assertNotEquals(userVm, new AuthenticatedUserVm("bob"));
        assertEquals(userVm.hashCode(), new AuthenticatedUserVm("alice").hashCode());

        assertNotNull(vm.toString());
        assertEquals(vm, new AuthenticationInfoVm(true, new AuthenticatedUserVm("alice")));
        assertNotEquals(vm, new AuthenticationInfoVm(false, new AuthenticatedUserVm("alice")));
        assertEquals(vm.hashCode(), new AuthenticationInfoVm(true, new AuthenticatedUserVm("alice")).hashCode());
    }
}
