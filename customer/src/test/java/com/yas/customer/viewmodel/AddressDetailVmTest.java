package com.yas.customer.viewmodel;

import com.yas.customer.viewmodel.address.AddressDetailVm;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressDetailVmTest {

    @Test
    void testConstructor_shouldCreateInstance() {
        // When
        AddressDetailVm addressDetailVm = new AddressDetailVm(
                1L, "John Doe", "123-456-7890", "123 Main Street",
                "Springfield", "12345", 1L, "Downtown", 10L,
                "Illinois", 100L, "United States");

        // Then
        assertThat(addressDetailVm).isNotNull();
        assertThat(addressDetailVm.id()).isEqualTo(1L);
        assertThat(addressDetailVm.contactName()).isEqualTo("John Doe");
        assertThat(addressDetailVm.phone()).isEqualTo("123-456-7890");
        assertThat(addressDetailVm.addressLine1()).isEqualTo("123 Main Street");
        assertThat(addressDetailVm.city()).isEqualTo("Springfield");
        assertThat(addressDetailVm.zipCode()).isEqualTo("12345");
        assertThat(addressDetailVm.districtId()).isEqualTo(1L);
        assertThat(addressDetailVm.districtName()).isEqualTo("Downtown");
        assertThat(addressDetailVm.stateOrProvinceId()).isEqualTo(10L);
        assertThat(addressDetailVm.stateOrProvinceName()).isEqualTo("Illinois");
        assertThat(addressDetailVm.countryId()).isEqualTo(100L);
        assertThat(addressDetailVm.countryName()).isEqualTo("United States");
    }

    @Test
    void testConstructor_withNullValues_shouldAccept() {
        // When
        AddressDetailVm addressDetailVm = new AddressDetailVm(
                null, null, null, null, null, null, null, null, null, null, null, null);

        // Then
        assertThat(addressDetailVm).isNotNull();
        assertThat(addressDetailVm.id()).isNull();
        assertThat(addressDetailVm.contactName()).isNull();
        assertThat(addressDetailVm.phone()).isNull();
    }

    @Test
    void testConstructor_withEmptyStrings_shouldWork() {
        // When
        AddressDetailVm addressDetailVm = new AddressDetailVm(
                2L, "", "", "", "", "", 2L, "", 20L, "", 200L, "");

        // Then
        assertThat(addressDetailVm).isNotNull();
        assertThat(addressDetailVm.id()).isEqualTo(2L);
        assertThat(addressDetailVm.contactName()).isEmpty();
        assertThat(addressDetailVm.districtName()).isEmpty();
    }
}
