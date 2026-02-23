package com.yas.customer.viewmodel;

import com.yas.customer.viewmodel.address.AddressPostVm;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressPostVmTest {

    @Test
    void testConstructor_shouldCreateInstance() {
        // When
        AddressPostVm addressPostVm = new AddressPostVm(
                "John Doe", "123-456-7890", "123 Main Street",
                "Springfield", "12345", 1L, 10L, 100L);

        // Then
        assertThat(addressPostVm).isNotNull();
        assertThat(addressPostVm.contactName()).isEqualTo("John Doe");
        assertThat(addressPostVm.phone()).isEqualTo("123-456-7890");
        assertThat(addressPostVm.addressLine1()).isEqualTo("123 Main Street");
        assertThat(addressPostVm.city()).isEqualTo("Springfield");
        assertThat(addressPostVm.zipCode()).isEqualTo("12345");
        assertThat(addressPostVm.districtId()).isEqualTo(1L);
        assertThat(addressPostVm.stateOrProvinceId()).isEqualTo(10L);
        assertThat(addressPostVm.countryId()).isEqualTo(100L);
    }

    @Test
    void testConstructor_withNullValues_shouldAccept() {
        // When
        AddressPostVm addressPostVm = new AddressPostVm(
                null, null, null, null, null, null, null, null);

        // Then
        assertThat(addressPostVm).isNotNull();
        assertThat(addressPostVm.contactName()).isNull();
        assertThat(addressPostVm.phone()).isNull();
        assertThat(addressPostVm.addressLine1()).isNull();
        assertThat(addressPostVm.city()).isNull();
        assertThat(addressPostVm.zipCode()).isNull();
        assertThat(addressPostVm.districtId()).isNull();
        assertThat(addressPostVm.stateOrProvinceId()).isNull();
        assertThat(addressPostVm.countryId()).isNull();
    }

    @Test
    void testConstructor_withEmptyStrings_shouldWork() {
        // When
        AddressPostVm addressPostVm = new AddressPostVm(
                "", "", "", "", "", 2L, 20L, 200L);

        // Then
        assertThat(addressPostVm).isNotNull();
        assertThat(addressPostVm.contactName()).isEmpty();
        assertThat(addressPostVm.phone()).isEmpty();
        assertThat(addressPostVm.addressLine1()).isEmpty();
        assertThat(addressPostVm.districtId()).isEqualTo(2L);
    }
}
