package com.yas.promotion.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConstantsTest {

    @Test
    void testConstantsInstantiation() {
        // Just to cover the default constructor of Constants and inner classes
        Constants constants = new Constants();
        assertNotNull(constants);

        Constants.ErrorCode errorCode = constants.new ErrorCode();
        assertNotNull(errorCode);

        Constants.Pageable pageable = constants.new Pageable();
        assertNotNull(pageable);
    }
}
