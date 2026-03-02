package com.yas.payment.viewmodel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CapturePaymentRequestVmTest {

    @Test
    void testCapturePaymentRequestVmBuilder() {
        // Given & When
        CapturePaymentRequestVm vm = CapturePaymentRequestVm.builder()
                .paymentMethod("PAYPAL")
                .token("token123")
                .build();

        // Then
        assertNotNull(vm);
        assertEquals("PAYPAL", vm.paymentMethod());
        assertEquals("token123", vm.token());
    }

    @Test
    void testCapturePaymentRequestVmWithNullValues() {
        // Given & When
        CapturePaymentRequestVm vm = CapturePaymentRequestVm.builder()
                .paymentMethod(null)
                .token(null)
                .build();

        // Then
        assertNotNull(vm);
        assertNull(vm.paymentMethod());
        assertNull(vm.token());
    }

    @Test
    void testCapturePaymentRequestVmEquality() {
        // Given
        CapturePaymentRequestVm vm1 = CapturePaymentRequestVm.builder()
                .paymentMethod("COD")
                .token("token456")
                .build();

        CapturePaymentRequestVm vm2 = CapturePaymentRequestVm.builder()
                .paymentMethod("COD")
                .token("token456")
                .build();

        // Then
        assertEquals(vm1, vm2);
        assertEquals(vm1.hashCode(), vm2.hashCode());
    }

    @Test
    void testCapturePaymentRequestVmToString() {
        // Given
        CapturePaymentRequestVm vm = CapturePaymentRequestVm.builder()
                .paymentMethod("BANKING")
                .token("token789")
                .build();

        // When & Then
        assertNotNull(vm.toString());
        assertTrue(vm.toString().contains("BANKING"));
    }
}
