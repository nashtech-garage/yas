package com.yas.customer.viewmodel;

import com.yas.customer.viewmodel.address.ActiveAddressVm;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ActiveAddressVmTest {

    @Test
    void testConstructor_shouldCreateInstance() {
        // When
        ActiveAddressVm activeAddressVm = new ActiveAddressVm(
                1L, "John Doe", "123456789", "123 Main St", "City",
                "12345", 10L, "District", 100L, "State", 1000L, "Country", true);

        // Then
        assertThat(activeAddressVm).isNotNull();
        assertThat(activeAddressVm.id()).isEqualTo(1L);
        assertThat(activeAddressVm.contactName()).isEqualTo("John Doe");
        assertThat(activeAddressVm.phone()).isEqualTo("123456789");
        assertThat(activeAddressVm.addressLine1()).isEqualTo("123 Main St");
        assertThat(activeAddressVm.city()).isEqualTo("City");
        assertThat(activeAddressVm.zipCode()).isEqualTo("12345");
        assertThat(activeAddressVm.isActive()).isTrue();
    }

    @Test
    void testConstructor_whenInactive_shouldWork() {
        // When
        ActiveAddressVm activeAddressVm = new ActiveAddressVm(
                2L, "Jane Smith", "987654321", "456 Oak Ave", "Town",
                "54321", 20L, "Area", 200L, "Province", 2000L, "Nation", false);

        // Then
        assertThat(activeAddressVm).isNotNull();
        assertThat(activeAddressVm.id()).isEqualTo(2L);
        assertThat(activeAddressVm.isActive()).isFalse();
    }

    @Test
    void testConstructor_withNullValues_shouldAccept() {
        // When
        ActiveAddressVm activeAddressVm = new ActiveAddressVm(
                null, null, null, null, null, null, null, null, null, null, null, null, null);

        // Then
        assertThat(activeAddressVm).isNotNull();
        assertThat(activeAddressVm.id()).isNull();
        assertThat(activeAddressVm.isActive()).isNull();
    }
}
