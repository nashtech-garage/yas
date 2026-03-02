package com.yas.payment.viewmodel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InitPaymentResponseVmTest {

    @Test
    void testInitPaymentResponseVmBuilder() {
        // Given & When
        InitPaymentResponseVm vm = InitPaymentResponseVm.builder()
                .status("SUCCESS")
                .paymentId("pay123")
                .redirectUrl("https://example.com/payment")
                .build();

        // Then
        assertNotNull(vm);
        assertEquals("SUCCESS", vm.status());
        assertEquals("pay123", vm.paymentId());
        assertEquals("https://example.com/payment", vm.redirectUrl());
    }

    @Test
    void testInitPaymentResponseVmWithNullValues() {
        // Given & When
        InitPaymentResponseVm vm = InitPaymentResponseVm.builder()
                .status(null)
                .paymentId(null)
                .redirectUrl(null)
                .build();

        // Then
        assertNotNull(vm);
        assertNull(vm.status());
        assertNull(vm.paymentId());
        assertNull(vm.redirectUrl());
    }

    @Test
    void testInitPaymentResponseVmEquality() {
        // Given
        InitPaymentResponseVm vm1 = InitPaymentResponseVm.builder()
                .status("PENDING")
                .paymentId("pay456")
                .redirectUrl("https://example.com/redirect")
                .build();

        InitPaymentResponseVm vm2 = InitPaymentResponseVm.builder()
                .status("PENDING")
                .paymentId("pay456")
                .redirectUrl("https://example.com/redirect")
                .build();

        // Then
        assertEquals(vm1, vm2);
        assertEquals(vm1.hashCode(), vm2.hashCode());
    }

    @Test
    void testInitPaymentResponseVmToString() {
        // Given
        InitPaymentResponseVm vm = InitPaymentResponseVm.builder()
                .status("COMPLETED")
                .paymentId("pay789")
                .redirectUrl("https://example.com/success")
                .build();

        // When & Then
        assertNotNull(vm.toString());
        assertTrue(vm.toString().contains("COMPLETED"));
    }
}
