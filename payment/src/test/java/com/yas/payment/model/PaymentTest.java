package com.yas.payment.model;

import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    void testPaymentBuilder() {
        // Given & When
        Payment payment = Payment.builder()
                .id(1L)
                .orderId(100L)
                .checkoutId("checkout123")
                .amount(BigDecimal.valueOf(999.99))
                .paymentFee(BigDecimal.valueOf(9.99))
                .paymentMethod(PaymentMethod.PAYPAL)
                .paymentStatus(PaymentStatus.COMPLETED)
                .gatewayTransactionId("txn123")
                .failureMessage(null)
                .build();

        // Then
        assertNotNull(payment);
        assertEquals(1L, payment.getId());
        assertEquals(100L, payment.getOrderId());
        assertEquals("checkout123", payment.getCheckoutId());
        assertEquals(BigDecimal.valueOf(999.99), payment.getAmount());
        assertEquals(BigDecimal.valueOf(9.99), payment.getPaymentFee());
        assertEquals(PaymentMethod.PAYPAL, payment.getPaymentMethod());
        assertEquals(PaymentStatus.COMPLETED, payment.getPaymentStatus());
        assertEquals("txn123", payment.getGatewayTransactionId());
        assertNull(payment.getFailureMessage());
    }

    @Test
    void testPaymentGettersAndSetters() {
        // Given
        Payment payment = new Payment();

        // When
        payment.setId(2L);
        payment.setOrderId(200L);
        payment.setCheckoutId("checkout456");
        payment.setAmount(BigDecimal.valueOf(1500.00));
        payment.setPaymentFee(BigDecimal.valueOf(15.00));
        payment.setPaymentMethod(PaymentMethod.COD);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setGatewayTransactionId("txn456");
        payment.setFailureMessage("Payment failed");

        // Then
        assertEquals(2L, payment.getId());
        assertEquals(200L, payment.getOrderId());
        assertEquals("checkout456", payment.getCheckoutId());
        assertEquals(BigDecimal.valueOf(1500.00), payment.getAmount());
        assertEquals(BigDecimal.valueOf(15.00), payment.getPaymentFee());
        assertEquals(PaymentMethod.COD, payment.getPaymentMethod());
        assertEquals(PaymentStatus.PENDING, payment.getPaymentStatus());
        assertEquals("txn456", payment.getGatewayTransactionId());
        assertEquals("Payment failed", payment.getFailureMessage());
    }

    @Test
    void testPaymentNoArgsConstructor() {
        // When
        Payment payment = new Payment();

        // Then
        assertNotNull(payment);
        assertNull(payment.getId());
        assertNull(payment.getOrderId());
        assertNull(payment.getCheckoutId());
    }

    @Test
    void testPaymentAllArgsConstructor() {
        // When
        Payment payment = new Payment(
                1L, 100L, "checkout789",
                BigDecimal.valueOf(500.00),
                BigDecimal.valueOf(5.00),
                PaymentMethod.BANKING,
                PaymentStatus.CANCELLED,
                "txn789",
                "Cancelled by user",
                "provider123"
        );

        // Then
        assertNotNull(payment);
        assertEquals(1L, payment.getId());
        assertEquals(100L, payment.getOrderId());
        assertEquals("checkout789", payment.getCheckoutId());
        assertEquals(BigDecimal.valueOf(500.00), payment.getAmount());
        assertEquals(BigDecimal.valueOf(5.00), payment.getPaymentFee());
        assertEquals(PaymentMethod.BANKING, payment.getPaymentMethod());
        assertEquals(PaymentStatus.CANCELLED, payment.getPaymentStatus());
        assertEquals("txn789", payment.getGatewayTransactionId());
        assertEquals("Cancelled by user", payment.getFailureMessage());
    }

    @Test
    void testPaymentMethodEnum() {
        Payment payment = new Payment();
        
        payment.setPaymentMethod(PaymentMethod.COD);
        assertEquals(PaymentMethod.COD, payment.getPaymentMethod());
        
        payment.setPaymentMethod(PaymentMethod.BANKING);
        assertEquals(PaymentMethod.BANKING, payment.getPaymentMethod());
        
        payment.setPaymentMethod(PaymentMethod.PAYPAL);
        assertEquals(PaymentMethod.PAYPAL, payment.getPaymentMethod());
    }

    @Test
    void testPaymentStatusEnum() {
        Payment payment = new Payment();
        
        payment.setPaymentStatus(PaymentStatus.PENDING);
        assertEquals(PaymentStatus.PENDING, payment.getPaymentStatus());
        
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        assertEquals(PaymentStatus.COMPLETED, payment.getPaymentStatus());
        
        payment.setPaymentStatus(PaymentStatus.CANCELLED);
        assertEquals(PaymentStatus.CANCELLED, payment.getPaymentStatus());
    }
}
