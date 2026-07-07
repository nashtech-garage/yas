package com.yas.customer.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConstantsTest {

    @Test
    void testConstructors() {
        Constants constants = new Constants();
        assertNotNull(constants);
        
        Constants.ErrorCode errorCode = new Constants.ErrorCode();
        assertNotNull(errorCode);
    }
}
