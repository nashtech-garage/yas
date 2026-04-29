package com.yas.customer.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ConstantsTest {

    @Test
    void testErrorCodes() {
        assertEquals("USER_WITH_EMAIL_NOT_FOUND", Constants.ErrorCode.USER_WITH_EMAIL_NOT_FOUND);
        assertEquals("USER_WITH_USERNAME_NOT_FOUND", Constants.ErrorCode.USER_WITH_USERNAME_NOT_FOUND);
        assertEquals("WRONG_EMAIL_FORMAT", Constants.ErrorCode.WRONG_EMAIL_FORMAT);
        assertEquals("USER_NOT_FOUND", Constants.ErrorCode.USER_NOT_FOUND);
        assertEquals("USER_ADDRESS_NOT_FOUND", Constants.ErrorCode.USER_ADDRESS_NOT_FOUND);
        assertEquals("ACTION FAILED, PLEASE LOGIN", Constants.ErrorCode.UNAUTHENTICATED);
        assertEquals("USERNAME_ALREADY_EXITED", Constants.ErrorCode.USERNAME_ALREADY_EXITED);
        assertEquals("USER_WITH_EMAIL_ALREADY_EXITED", Constants.ErrorCode.USER_WITH_EMAIL_ALREADY_EXITED);
    }
}
