package com.yas.sampledata.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ViewModelTest {

    // ---- SampleDataVm ----
    @Test
    void sampleDataVm_shouldStoreMessage() {
        SampleDataVm vm = new SampleDataVm("hello");
        assertThat(vm.message()).isEqualTo("hello");
    }

    @Test
    void sampleDataVm_withNullMessage_shouldAllowNull() {
        SampleDataVm vm = new SampleDataVm(null);
        assertThat(vm.message()).isNull();
    }

    @Test
    void sampleDataVm_equalsSameValue_shouldBeEqual() {
        SampleDataVm a = new SampleDataVm("msg");
        SampleDataVm b = new SampleDataVm("msg");
        assertThat(a).isEqualTo(b);
    }

    @Test
    void sampleDataVm_toString_shouldContainMessage() {
        SampleDataVm vm = new SampleDataVm("test-message");
        assertThat(vm.toString()).contains("test-message");
    }

    // ---- ErrorVm ----
    @Test
    void errorVm_threeArgsConstructor_shouldInitializeWithEmptyFieldErrors() {
        ErrorVm vm = new ErrorVm("400", "Bad Request", "Some detail");
        assertThat(vm.statusCode()).isEqualTo("400");
        assertThat(vm.title()).isEqualTo("Bad Request");
        assertThat(vm.detail()).isEqualTo("Some detail");
        assertThat(vm.fieldErrors()).isEmpty();
    }

    @Test
    void errorVm_fourArgsConstructor_shouldStoreAllFields() {
        List<String> errors = List.of("field1 is required", "field2 is invalid");
        ErrorVm vm = new ErrorVm("422", "Validation Error", "Validation failed", errors);
        assertThat(vm.statusCode()).isEqualTo("422");
        assertThat(vm.fieldErrors()).hasSize(2);
        assertThat(vm.fieldErrors()).contains("field1 is required", "field2 is invalid");
    }

    @Test
    void errorVm_equalsSameValues_shouldBeEqual() {
        ErrorVm a = new ErrorVm("500", "Error", "detail");
        ErrorVm b = new ErrorVm("500", "Error", "detail");
        assertThat(a).isEqualTo(b);
    }

    @Test
    void errorVm_toString_shouldContainStatusCode() {
        ErrorVm vm = new ErrorVm("404", "Not Found", "Resource not found");
        assertThat(vm.toString()).contains("404");
    }
}
