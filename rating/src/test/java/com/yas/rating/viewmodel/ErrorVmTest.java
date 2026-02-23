package com.yas.rating.viewmodel;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorVmTest {

    @Test
    void testConstructor_withAllParameters_shouldCreateErrorVm() {
        // Given
        List<String> fieldErrors = Arrays.asList("Field1 error", "Field2 error");

        // When
        ErrorVm errorVm = new ErrorVm("400", "Bad Request", "Invalid input", fieldErrors);

        // Then
        assertThat(errorVm).isNotNull();
        assertThat(errorVm.statusCode()).isEqualTo("400");
        assertThat(errorVm.title()).isEqualTo("Bad Request");
        assertThat(errorVm.detail()).isEqualTo("Invalid input");
        assertThat(errorVm.fieldErrors()).hasSize(2);
        assertThat(errorVm.fieldErrors()).contains("Field1 error", "Field2 error");
    }

    @Test
    void testConstructor_withoutFieldErrors_shouldCreateEmptyFieldErrorsList() {
        // When
        ErrorVm errorVm = new ErrorVm("404", "Not Found", "Resource not found");

        // Then
        assertThat(errorVm).isNotNull();
        assertThat(errorVm.statusCode()).isEqualTo("404");
        assertThat(errorVm.title()).isEqualTo("Not Found");
        assertThat(errorVm.detail()).isEqualTo("Resource not found");
        assertThat(errorVm.fieldErrors()).isNotNull();
        assertThat(errorVm.fieldErrors()).isEmpty();
    }

    @Test
    void testConstructor_withEmptyFieldErrors_shouldWork() {
        // Given
        List<String> fieldErrors = new ArrayList<>();

        // When
        ErrorVm errorVm = new ErrorVm("500", "Internal Server Error", "Server error", fieldErrors);

        // Then
        assertThat(errorVm.fieldErrors()).isEmpty();
    }

    @Test
    void testConstructor_withNullValues_shouldAcceptNull() {
        // When
        ErrorVm errorVm = new ErrorVm(null, null, null);

        // Then
        assertThat(errorVm.statusCode()).isNull();
        assertThat(errorVm.title()).isNull();
        assertThat(errorVm.detail()).isNull();
        assertThat(errorVm.fieldErrors()).isNotNull();
        assertThat(errorVm.fieldErrors()).isEmpty();
    }

    @Test
    void testConstructor_withSingleFieldError_shouldWork() {
        // Given
        List<String> fieldErrors = List.of("Email is required");

        // When
        ErrorVm errorVm = new ErrorVm("400", "Validation Error", "Validation failed", fieldErrors);

        // Then
        assertThat(errorVm.fieldErrors()).hasSize(1);
        assertThat(errorVm.fieldErrors().get(0)).isEqualTo("Email is required");
    }
}
