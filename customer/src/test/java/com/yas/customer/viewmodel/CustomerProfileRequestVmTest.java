package com.yas.customer.viewmodel;

import com.yas.customer.viewmodel.customer.CustomerProfileRequestVm;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerProfileRequestVmTest {

    @Test
    void testConstructor_shouldCreateInstance() {
        // When
        CustomerProfileRequestVm requestVm = new CustomerProfileRequestVm(
                "John", "Doe", "john.doe@example.com");

        // Then
        assertThat(requestVm).isNotNull();
        assertThat(requestVm.firstName()).isEqualTo("John");
        assertThat(requestVm.lastName()).isEqualTo("Doe");
        assertThat(requestVm.email()).isEqualTo("john.doe@example.com");
    }

    @Test
    void testConstructor_withNullValues_shouldAccept() {
        // When
        CustomerProfileRequestVm requestVm = new CustomerProfileRequestVm(null, null, null);

        // Then
        assertThat(requestVm).isNotNull();
        assertThat(requestVm.firstName()).isNull();
        assertThat(requestVm.lastName()).isNull();
        assertThat(requestVm.email()).isNull();
    }

    @Test
    void testConstructor_withEmptyStrings_shouldWork() {
        // When
        CustomerProfileRequestVm requestVm = new CustomerProfileRequestVm("", "", "");

        // Then
        assertThat(requestVm).isNotNull();
        assertThat(requestVm.firstName()).isEmpty();
        assertThat(requestVm.lastName()).isEmpty();
        assertThat(requestVm.email()).isEmpty();
    }
}
