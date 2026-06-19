package com.yas.rating.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConstantsTest {
    @Test
    void testConstants() {
        assertEquals("RATING_NOT_FOUND", Constants.ErrorCode.RATING_NOT_FOUND);
        assertEquals("PRODUCT_NOT_FOUND", Constants.ErrorCode.PRODUCT_NOT_FOUND);
        assertEquals("CUSTOMER_NOT_FOUND", Constants.ErrorCode.CUSTOMER_NOT_FOUND);
        assertEquals("RESOURCE_ALREADY_EXISTED", Constants.ErrorCode.RESOURCE_ALREADY_EXISTED);
        assertEquals("ACCESS_DENIED", Constants.ErrorCode.ACCESS_DENIED);
        assertEquals("SUCCESS", Constants.Message.SUCCESS_MESSAGE);
    }

    @Test
    void testConstructors() {
        assertNotNull(new Constants());
        assertNotNull(new Constants().new ErrorCode());
        assertNotNull(new Constants().new Message());
    }
}
