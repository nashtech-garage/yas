package com.yas.payment.viewmodel.paymentprovider;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentProviderVmTest {

    @Test
    void testPaymentProviderVmConstructor() {
        // Given & When
        PaymentProviderVm vm = new PaymentProviderVm(
                "paypal",
                "PayPal",
                "/paypal/config",
                1,
                100L,
                "https://example.com/icon.png"
        );

        // Then
        assertNotNull(vm);
        assertEquals("paypal", vm.getId());
        assertEquals("PayPal", vm.getName());
        assertEquals("/paypal/config", vm.getConfigureUrl());
        assertEquals(1, vm.getVersion());
        assertEquals(100L, vm.getMediaId());
        assertEquals("https://example.com/icon.png", vm.getIconUrl());
    }

    @Test
    void testPaymentProviderVmGettersAndSetters() {
        // Given
        PaymentProviderVm vm = new PaymentProviderVm(
                "stripe",
                "Stripe",
                "/stripe/config",
                2,
                200L,
                "https://example.com/stripe.png"
        );

        // When
        vm.setId("new-stripe");
        vm.setName("Stripe Payment");
        vm.setConfigureUrl("/stripe/new-config");
        vm.setVersion(3);
        vm.setMediaId(300L);
        vm.setIconUrl("https://example.com/new-icon.png");

        // Then
        assertEquals("new-stripe", vm.getId());
        assertEquals("Stripe Payment", vm.getName());
        assertEquals("/stripe/new-config", vm.getConfigureUrl());
        assertEquals(3, vm.getVersion());
        assertEquals(300L, vm.getMediaId());
        assertEquals("https://example.com/new-icon.png", vm.getIconUrl());
    }

    @Test
    void testPaymentProviderVmWithNullValues() {
        // Given & When
        PaymentProviderVm vm = new PaymentProviderVm(
                null,
                null,
                null,
                0,
                null,
                null
        );

        // Then
        assertNotNull(vm);
        assertNull(vm.getId());
        assertNull(vm.getName());
        assertNull(vm.getConfigureUrl());
        assertEquals(0, vm.getVersion());
        assertNull(vm.getMediaId());
        assertNull(vm.getIconUrl());
    }
}
