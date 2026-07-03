package com.yas.payment.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConstantsTest {

    @Test
    void testErrorCodeConstants_ShouldHaveCorrectValues() {
        // Then
        assertThat(Constants.ErrorCode.PAYMENT_PROVIDER_NOT_FOUND)
            .isEqualTo("PAYMENT_PROVIDER_NOT_FOUND");
    }

    @Test
    void testMessageConstants_ShouldHaveCorrectValues() {
        // Then
        assertThat(Constants.Message.SUCCESS_MESSAGE)
            .isEqualTo("SUCCESS");
    }

    @Test
    void testErrorCodeConstants_ShouldNotBeNull() {
        // Then
        assertThat(Constants.ErrorCode.PAYMENT_PROVIDER_NOT_FOUND)
            .isNotNull()
            .isNotBlank();
    }

    @Test
    void testMessageConstants_ShouldNotBeNull() {
        // Then
        assertThat(Constants.Message.SUCCESS_MESSAGE)
            .isNotNull()
            .isNotBlank();
    }

    @Test
    void testErrorCodeConstants_ShouldBeEqual() {
        // Given
        String expected = "PAYMENT_PROVIDER_NOT_FOUND";
        
        // Then
        assertThat(Constants.ErrorCode.PAYMENT_PROVIDER_NOT_FOUND)
            .isEqualTo(expected);
    }

    @Test
    void testMessageConstants_ShouldBeEqual() {
        // Given
        String expected = "SUCCESS";
        
        // Then
        assertThat(Constants.Message.SUCCESS_MESSAGE)
            .isEqualTo(expected);
    }
}