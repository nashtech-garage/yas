package com.yas.payment.model.enumeration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentMethodTest {

    @Test
    void testPaymentMethodValues() {
        PaymentMethod[] methods = PaymentMethod.values();
        
        assertEquals(3, methods.length);
        assertEquals(PaymentMethod.COD, methods[0]);
        assertEquals(PaymentMethod.BANKING, methods[1]);
        assertEquals(PaymentMethod.PAYPAL, methods[2]);
    }

    @Test
    void testPaymentMethodValueOf() {
        assertEquals(PaymentMethod.COD, PaymentMethod.valueOf("COD"));
        assertEquals(PaymentMethod.BANKING, PaymentMethod.valueOf("BANKING"));
        assertEquals(PaymentMethod.PAYPAL, PaymentMethod.valueOf("PAYPAL"));
    }

    @Test
    void testPaymentMethodName() {
        assertEquals("COD", PaymentMethod.COD.name());
        assertEquals("BANKING", PaymentMethod.BANKING.name());
        assertEquals("PAYPAL", PaymentMethod.PAYPAL.name());
    }
}
