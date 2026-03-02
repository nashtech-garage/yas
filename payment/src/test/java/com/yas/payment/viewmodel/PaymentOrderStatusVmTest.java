package com.yas.payment.viewmodel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentOrderStatusVmTest {

    @Test
    void testPaymentOrderStatusVmBuilder() {
        // Given & When
        PaymentOrderStatusVm vm = PaymentOrderStatusVm.builder()
                .orderId(100L)
                .orderStatus("COMPLETED")
                .paymentId(200L)
                .paymentStatus("SUCCESS")
                .build();

        // Then
        assertNotNull(vm);
        assertEquals(100L, vm.orderId());
        assertEquals("COMPLETED", vm.orderStatus());
        assertEquals(200L, vm.paymentId());
        assertEquals("SUCCESS", vm.paymentStatus());
    }

    @Test
    void testPaymentOrderStatusVmWithNullValues() {
        // Given & When
        PaymentOrderStatusVm vm = PaymentOrderStatusVm.builder()
                .orderId(null)
                .orderStatus(null)
                .paymentId(null)
                .paymentStatus(null)
                .build();

        // Then
        assertNotNull(vm);
        assertNull(vm.orderId());
        assertNull(vm.orderStatus());
        assertNull(vm.paymentId());
        assertNull(vm.paymentStatus());
    }

    @Test
    void testPaymentOrderStatusVmEquality() {
        // Given
        PaymentOrderStatusVm vm1 = PaymentOrderStatusVm.builder()
                .orderId(300L)
                .orderStatus("PENDING")
                .paymentId(400L)
                .paymentStatus("PROCESSING")
                .build();

        PaymentOrderStatusVm vm2 = PaymentOrderStatusVm.builder()
                .orderId(300L)
                .orderStatus("PENDING")
                .paymentId(400L)
                .paymentStatus("PROCESSING")
                .build();

        // Then
        assertEquals(vm1, vm2);
        assertEquals(vm1.hashCode(), vm2.hashCode());
    }

    @Test
    void testPaymentOrderStatusVmToString() {
        // Given
        PaymentOrderStatusVm vm = PaymentOrderStatusVm.builder()
                .orderId(500L)
                .orderStatus("CANCELLED")
                .paymentId(600L)
                .paymentStatus("FAILED")
                .build();

        // When & Then
        assertNotNull(vm.toString());
        assertTrue(vm.toString().contains("500"));
    }

    @Test
    void testPaymentOrderStatusVmWithDifferentValues() {
        // Given & When
        PaymentOrderStatusVm vm1 = PaymentOrderStatusVm.builder()
                .orderId(700L)
                .orderStatus("PENDING")
                .paymentId(800L)
                .paymentStatus("PENDING")
                .build();

        PaymentOrderStatusVm vm2 = PaymentOrderStatusVm.builder()
                .orderId(700L)
                .orderStatus("COMPLETED")
                .paymentId(800L)
                .paymentStatus("SUCCESS")
                .build();

        // Then
        assertNotEquals(vm1, vm2);
        assertEquals(vm1.orderId(), vm2.orderId());
        assertEquals(vm1.paymentId(), vm2.paymentId());
        assertNotEquals(vm1.orderStatus(), vm2.orderStatus());
    }
}
