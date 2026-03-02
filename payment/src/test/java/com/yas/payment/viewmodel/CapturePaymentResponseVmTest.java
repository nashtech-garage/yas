package com.yas.payment.viewmodel;

import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CapturePaymentResponseVmTest {

    @Test
    void testCapturePaymentResponseVmBuilder() {
        // Given & When
        CapturePaymentResponseVm vm = CapturePaymentResponseVm.builder()
                .orderId(100L)
                .checkoutId("checkout123")
                .amount(BigDecimal.valueOf(500.00))
                .paymentFee(BigDecimal.valueOf(5.00))
                .gatewayTransactionId("txn123")
                .paymentMethod(PaymentMethod.PAYPAL)
                .paymentStatus(PaymentStatus.COMPLETED)
                .failureMessage(null)
                .build();

        // Then
        assertNotNull(vm);
        assertEquals(100L, vm.orderId());
        assertEquals("checkout123", vm.checkoutId());
        assertEquals(BigDecimal.valueOf(500.00), vm.amount());
        assertEquals(BigDecimal.valueOf(5.00), vm.paymentFee());
        assertEquals("txn123", vm.gatewayTransactionId());
        assertEquals(PaymentMethod.PAYPAL, vm.paymentMethod());
        assertEquals(PaymentStatus.COMPLETED, vm.paymentStatus());
        assertNull(vm.failureMessage());
    }

    @Test
    void testCapturePaymentResponseVmWithFailure() {
        // Given & When
        CapturePaymentResponseVm vm = CapturePaymentResponseVm.builder()
                .orderId(200L)
                .checkoutId("checkout456")
                .amount(BigDecimal.valueOf(100.00))
                .paymentFee(BigDecimal.ZERO)
                .gatewayTransactionId("txn456")
                .paymentMethod(PaymentMethod.COD)
                .paymentStatus(PaymentStatus.CANCELLED)
                .failureMessage("Payment cancelled by user")
                .build();

        // Then
        assertNotNull(vm);
        assertEquals(200L, vm.orderId());
        assertEquals(PaymentStatus.CANCELLED, vm.paymentStatus());
        assertEquals("Payment cancelled by user", vm.failureMessage());
    }

    @Test
    void testCapturePaymentResponseVmEquality() {
        // Given
        CapturePaymentResponseVm vm1 = CapturePaymentResponseVm.builder()
                .orderId(300L)
                .checkoutId("checkout789")
                .amount(BigDecimal.valueOf(250.00))
                .paymentFee(BigDecimal.valueOf(2.50))
                .gatewayTransactionId("txn789")
                .paymentMethod(PaymentMethod.BANKING)
                .paymentStatus(PaymentStatus.PENDING)
                .failureMessage(null)
                .build();

        CapturePaymentResponseVm vm2 = CapturePaymentResponseVm.builder()
                .orderId(300L)
                .checkoutId("checkout789")
                .amount(BigDecimal.valueOf(250.00))
                .paymentFee(BigDecimal.valueOf(2.50))
                .gatewayTransactionId("txn789")
                .paymentMethod(PaymentMethod.BANKING)
                .paymentStatus(PaymentStatus.PENDING)
                .failureMessage(null)
                .build();

        // Then
        assertEquals(vm1, vm2);
        assertEquals(vm1.hashCode(), vm2.hashCode());
    }

    @Test
    void testCapturePaymentResponseVmToString() {
        // Given
        CapturePaymentResponseVm vm = CapturePaymentResponseVm.builder()
                .orderId(400L)
                .checkoutId("checkout999")
                .amount(BigDecimal.valueOf(1000.00))
                .paymentFee(BigDecimal.valueOf(10.00))
                .gatewayTransactionId("txn999")
                .paymentMethod(PaymentMethod.PAYPAL)
                .paymentStatus(PaymentStatus.COMPLETED)
                .failureMessage(null)
                .build();

        // When & Then
        assertNotNull(vm.toString());
        assertTrue(vm.toString().contains("400"));
    }
}
