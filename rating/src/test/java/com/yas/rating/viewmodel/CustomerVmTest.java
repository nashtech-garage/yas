package com.yas.rating.viewmodel;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerVmTest {

    @Test
    void testConstructor_shouldCreateCustomerVm() {
        // When
        CustomerVm customerVm = new CustomerVm(
                "johndoe",
                "john.doe@example.com",
                "John",
                "Doe");

        // Then
        assertThat(customerVm).isNotNull();
        assertThat(customerVm.username()).isEqualTo("johndoe");
        assertThat(customerVm.email()).isEqualTo("john.doe@example.com");
        assertThat(customerVm.firstName()).isEqualTo("John");
        assertThat(customerVm.lastName()).isEqualTo("Doe");
    }

    @Test
    void testConstructor_withNullValues_shouldAcceptNull() {
        // When
        CustomerVm customerVm = new CustomerVm(null, null, null, null);

        // Then
        assertThat(customerVm.username()).isNull();
        assertThat(customerVm.email()).isNull();
        assertThat(customerVm.firstName()).isNull();
        assertThat(customerVm.lastName()).isNull();
    }

    @Test
    void testConstructor_withPartialData_shouldWork() {
        // When
        CustomerVm customerVm = new CustomerVm(
                "janesmith",
                "jane@example.com",
                null,
                null);

        // Then
        assertThat(customerVm.username()).isEqualTo("janesmith");
        assertThat(customerVm.email()).isEqualTo("jane@example.com");
        assertThat(customerVm.firstName()).isNull();
        assertThat(customerVm.lastName()).isNull();
    }
}
