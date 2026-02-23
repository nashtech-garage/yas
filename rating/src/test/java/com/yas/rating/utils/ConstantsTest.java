package com.yas.rating.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConstantsTest {

    @Test
    void testErrorCodeConstants_shouldHaveCorrectValues() {
        assertThat(Constants.ErrorCode.RATING_NOT_FOUND).isEqualTo("RATING_NOT_FOUND");
        assertThat(Constants.ErrorCode.PRODUCT_NOT_FOUND).isEqualTo("PRODUCT_NOT_FOUND");
        assertThat(Constants.ErrorCode.CUSTOMER_NOT_FOUND).isEqualTo("CUSTOMER_NOT_FOUND");
        assertThat(Constants.ErrorCode.RESOURCE_ALREADY_EXISTED).isEqualTo("RESOURCE_ALREADY_EXISTED");
        assertThat(Constants.ErrorCode.ACCESS_DENIED).isEqualTo("ACCESS_DENIED");
    }

    @Test
    void testMessageConstants_shouldHaveCorrectValues() {
        assertThat(Constants.Message.SUCCESS_MESSAGE).isEqualTo("SUCCESS");
    }
}
