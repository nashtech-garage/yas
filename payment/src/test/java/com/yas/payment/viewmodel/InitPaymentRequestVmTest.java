package com.yas.payment.viewmodel;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class InitPaymentRequestVmTest {

    @Test
    void testInitPaymentRequestVmBuilder() {
        // Given & When
        InitPaymentRequestVm vm = InitPaymentRequestVm.builder()
                .paymentMethod("PAYPAL")
                .totalPrice(BigDecimal.valueOf(100.00))
                .checkoutId("checkout123")
                .build();

        // Then
        assertNotNull(vm);
        assertEquals("PAYPAL", vm.paymentMethod());
        assertEquals(BigDecimal.valueOf(100.00), vm.totalPrice());
        assertEquals("checkout123", vm.checkoutId());
    }

    @Test
    void testInitPaymentRequestVmWithNullValues() {
        // Given & When
        InitPaymentRequestVm vm = InitPaymentRequestVm.builder()
                .paymentMethod(null)
                .totalPrice(null)
                .checkoutId(null)
                .build();

        // Then
        assertNotNull(vm);
        assertNull(vm.paymentMethod());
        assertNull(vm.totalPrice());
        assertNull(vm.checkoutId());
    }

    @Test
    void testInitPaymentRequestVmEquality() {
        // Given
        InitPaymentRequestVm vm1 = InitPaymentRequestVm.builder()
                .paymentMethod("COD")
                .totalPrice(BigDecimal.valueOf(50.00))
                .checkoutId("checkout456")
                .build();

        InitPaymentRequestVm vm2 = InitPaymentRequestVm.builder()
                .paymentMethod("COD")
                .totalPrice(BigDecimal.valueOf(50.00))
                .checkoutId("checkout456")
                .build();

        // Then
        assertEquals(vm1, vm2);
        assertEquals(vm1.hashCode(), vm2.hashCode());
    }

    @Test
    void testInitPaymentRequestVmToString() {
        // Given
        InitPaymentRequestVm vm = InitPaymentRequestVm.builder()
                .paymentMethod("BANKING")
                .totalPrice(BigDecimal.valueOf(200.00))
                .checkoutId("checkout789")
                .build();

        // When & Then
        assertNotNull(vm.toString());
        assertTrue(vm.toString().contains("BANKING"));
    }
}
