package com.yas.payment.viewmodel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutStatusVmTest {

    @Test
    void testCheckoutStatusVmConstructor() {
        // Given & When
        CheckoutStatusVm vm = new CheckoutStatusVm("checkout123", "COMPLETED");

        // Then
        assertNotNull(vm);
        assertEquals("checkout123", vm.checkoutId());
        assertEquals("COMPLETED", vm.checkoutStatus());
    }

    @Test
    void testCheckoutStatusVmWithNullValues() {
        // Given & When
        CheckoutStatusVm vm = new CheckoutStatusVm(null, null);

        // Then
        assertNotNull(vm);
        assertNull(vm.checkoutId());
        assertNull(vm.checkoutStatus());
    }

    @Test
    void testCheckoutStatusVmEquality() {
        // Given
        CheckoutStatusVm vm1 = new CheckoutStatusVm("checkout456", "PENDING");
        CheckoutStatusVm vm2 = new CheckoutStatusVm("checkout456", "PENDING");

        // Then
        assertEquals(vm1, vm2);
        assertEquals(vm1.hashCode(), vm2.hashCode());
    }

    @Test
    void testCheckoutStatusVmToString() {
        // Given
        CheckoutStatusVm vm = new CheckoutStatusVm("checkout789", "CANCELLED");

        // When & Then
        assertNotNull(vm.toString());
        assertTrue(vm.toString().contains("checkout789"));
    }

    @Test
    void testCheckoutStatusVmWithDifferentStatuses() {
        // Given & When
        CheckoutStatusVm vm1 = new CheckoutStatusVm("checkout001", "PENDING");
        CheckoutStatusVm vm2 = new CheckoutStatusVm("checkout001", "COMPLETED");

        // Then
        assertNotEquals(vm1, vm2);
        assertEquals("checkout001", vm1.checkoutId());
        assertEquals("PENDING", vm1.checkoutStatus());
        assertEquals("COMPLETED", vm2.checkoutStatus());
    }
}
