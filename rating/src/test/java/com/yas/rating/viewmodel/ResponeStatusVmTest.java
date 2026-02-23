package com.yas.rating.viewmodel;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResponeStatusVmTest {

    @Test
    void testConstructor_shouldCreateResponeStatusVm() {
        // When
        ResponeStatusVm responeStatusVm = new ResponeStatusVm("Success", "Operation completed", "200");

        // Then
        assertThat(responeStatusVm).isNotNull();
        assertThat(responeStatusVm.title()).isEqualTo("Success");
        assertThat(responeStatusVm.message()).isEqualTo("Operation completed");
        assertThat(responeStatusVm.statusCode()).isEqualTo("200");
    }

    @Test
    void testConstructor_withNullValues_shouldAcceptNull() {
        // When
        ResponeStatusVm responeStatusVm = new ResponeStatusVm(null, null, null);

        // Then
        assertThat(responeStatusVm.title()).isNull();
        assertThat(responeStatusVm.message()).isNull();
        assertThat(responeStatusVm.statusCode()).isNull();
    }

    @Test
    void testConstructor_withErrorStatus_shouldWork() {
        // When
        ResponeStatusVm responeStatusVm = new ResponeStatusVm(
                "Error",
                "Operation failed",
                "500");

        // Then
        assertThat(responeStatusVm.title()).isEqualTo("Error");
        assertThat(responeStatusVm.message()).isEqualTo("Operation failed");
        assertThat(responeStatusVm.statusCode()).isEqualTo("500");
    }
}
