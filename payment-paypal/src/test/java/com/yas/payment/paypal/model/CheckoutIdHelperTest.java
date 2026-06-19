package com.yas.payment.paypal.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CheckoutIdHelperTest {

    @Test
    void testSetAndGetCheckoutId() {
        // Given
        String testId = "test-checkout-123";

        // When
        CheckoutIdHelper.setCheckoutId(testId);

        // Then
        assertThat(CheckoutIdHelper.getCheckoutId()).isEqualTo(testId);
    }

    @Test
    void testSetAndGetCheckoutId_withNull() {
        // When
        CheckoutIdHelper.setCheckoutId(null);

        // Then
        assertThat(CheckoutIdHelper.getCheckoutId()).isNull();
    }
}
