package com.yas.customer.viewmodel;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorVmTest {

    @Test
    void testConstructor_withAllFields_shouldCreateInstance() {
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
    void testConstructor_withoutFieldErrors_shouldCreateEmptyList() {
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
        List<String> emptyFieldErrors = List.of();

        // When
        ErrorVm errorVm = new ErrorVm("500", "Internal Error", "Server error", emptyFieldErrors);

        // Then
        assertThat(errorVm).isNotNull();
        assertThat(errorVm.statusCode()).isEqualTo("500");
        assertThat(errorVm.fieldErrors()).isEmpty();
    }

    @Test
    void testConstructor_withSingleFieldError_shouldCreateList() {
        // Given
        List<String> singleError = List.of("Email is required");

        // When
        ErrorVm errorVm = new ErrorVm("400", "Validation Error", "Request validation failed", singleError);

        // Then
        assertThat(errorVm).isNotNull();
        assertThat(errorVm.fieldErrors()).hasSize(1);
        assertThat(errorVm.fieldErrors()).contains("Email is required");
    }

    @Test
    void testConstructor_withMultipleFieldErrors_shouldPreserveOrder() {
        // Given
        List<String> fieldErrors = Arrays.asList("Error 1", "Error 2", "Error 3");

        // When
        ErrorVm errorVm = new ErrorVm("422", "Unprocessable Entity", "Multiple validation errors", fieldErrors);

        // Then
        assertThat(errorVm).isNotNull();
        assertThat(errorVm.fieldErrors()).hasSize(3);
        assertThat(errorVm.fieldErrors().get(0)).isEqualTo("Error 1");
        assertThat(errorVm.fieldErrors().get(1)).isEqualTo("Error 2");
        assertThat(errorVm.fieldErrors().get(2)).isEqualTo("Error 3");
    }

    @Test
    void testConstructor_withNullValues_shouldAcceptNulls() {
        // When
        ErrorVm errorVm = new ErrorVm(null, null, null, null);

        // Then
        assertThat(errorVm).isNotNull();
        assertThat(errorVm.statusCode()).isNull();
        assertThat(errorVm.title()).isNull();
        assertThat(errorVm.detail()).isNull();
        assertThat(errorVm.fieldErrors()).isNull();
    }
}
