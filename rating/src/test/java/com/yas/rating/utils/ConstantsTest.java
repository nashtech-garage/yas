package com.yas.rating.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConstantsTest {

    @Test
    void testConstantsInstantiation() {
        Constants constants = new Constants();
        assertNotNull(constants);

        Constants.ErrorCode errorCode = constants.new ErrorCode();
        assertNotNull(errorCode);
    }
}
