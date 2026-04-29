package com.yas.customer.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ConstantsTest {

    @Test
    void testConstantsInstantiation() {
        Constants constants = new Constants();
        assertNotNull(constants);
        Constants.ErrorCode errorCode = new Constants.ErrorCode();
        assertNotNull(errorCode);
    }
}
