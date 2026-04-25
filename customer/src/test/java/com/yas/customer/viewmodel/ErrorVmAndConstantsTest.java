package com.yas.customer.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.customer.utils.Constants;
import org.junit.jupiter.api.Test;

class ErrorVmAndConstantsTest {

    @Test
    void errorVm_compactConstructor_shouldInitEmptyFieldErrors() {
        ErrorVm vm = new ErrorVm("400", "Bad Request", "detail");
        assertThat(vm.fieldErrors()).isNotNull();
        assertThat(vm.fieldErrors()).isEmpty();
    }

    @Test
    void constants_shouldExposeExpectedValues() {
        assertThat(Constants.ErrorCode.USER_ADDRESS_NOT_FOUND).isEqualTo("USER_ADDRESS_NOT_FOUND");
        assertThat(Constants.ErrorCode.UNAUTHENTICATED).contains("LOGIN");
    }
}
