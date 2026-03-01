package com.yas.tax.viewmodel.error;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ErrorVmTest {

    @Test
    void defaultConstructor_shouldInitEmptyFieldErrors() {
        ErrorVm vm = new ErrorVm("400", "Bad Request", "detail");

        assertThat(vm.fieldErrors()).isEmpty();
        assertThat(vm.statusCode()).isEqualTo("400");
    }

    @Test
    void fullConstructor_shouldKeepProvidedFieldErrors() {
        List<String> errors = List.of("name", "zipCode");

        ErrorVm vm = new ErrorVm("422", "Invalid", "fail", errors);

        assertThat(vm.fieldErrors()).containsExactly("name", "zipCode");
        assertThat(vm.title()).isEqualTo("Invalid");
    }
}
