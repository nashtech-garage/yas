package com.yas.customer.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConstantsTest {

    @Test
    void testErrorCodeConstants_shouldHaveCorrectValues() {
        assertThat(Constants.ErrorCode.USER_NOT_FOUND).isEqualTo("USER_NOT_FOUND");
        assertThat(Constants.ErrorCode.USER_WITH_EMAIL_NOT_FOUND).isEqualTo("USER_WITH_EMAIL_NOT_FOUND");
        assertThat(Constants.ErrorCode.WRONG_EMAIL_FORMAT).isEqualTo("WRONG_EMAIL_FORMAT");
        assertThat(Constants.ErrorCode.USERNAME_ALREADY_EXITED).isEqualTo("USERNAME_ALREADY_EXITED");
        assertThat(Constants.ErrorCode.USER_WITH_EMAIL_ALREADY_EXITED).isEqualTo("USER_WITH_EMAIL_ALREADY_EXITED");
        assertThat(Constants.ErrorCode.USER_ADDRESS_NOT_FOUND).isEqualTo("USER_ADDRESS_NOT_FOUND");
        assertThat(Constants.ErrorCode.UNAUTHENTICATED).isEqualTo("ACTION FAILED, PLEASE LOGIN");
    }

    @Test
    void testErrorCodeClass_canBeInstantiated() {
        Constants.ErrorCode errorCode = new Constants.ErrorCode();
        assertThat(errorCode).isNotNull();
    }
}
