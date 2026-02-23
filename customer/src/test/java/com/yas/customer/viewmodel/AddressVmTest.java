package com.yas.customer.viewmodel;

import com.yas.customer.viewmodel.address.AddressVm;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressVmTest {

    @Test
    void testBuilder_shouldCreateInstance() {
        // When
        AddressVm addressVm = AddressVm.builder()
                .id(1L)
                .contactName("John Doe")
                .phone("123-456-7890")
                .addressLine1("123 Main Street")
                .city("Springfield")
                .zipCode("12345")
                .districtId(1L)
                .stateOrProvinceId(10L)
                .countryId(100L)
                .build();

        // Then
        assertThat(addressVm).isNotNull();
        assertThat(addressVm.id()).isEqualTo(1L);
        assertThat(addressVm.contactName()).isEqualTo("John Doe");
        assertThat(addressVm.phone()).isEqualTo("123-456-7890");
        assertThat(addressVm.addressLine1()).isEqualTo("123 Main Street");
        assertThat(addressVm.city()).isEqualTo("Springfield");
        assertThat(addressVm.zipCode()).isEqualTo("12345");
        assertThat(addressVm.districtId()).isEqualTo(1L);
        assertThat(addressVm.stateOrProvinceId()).isEqualTo(10L);
        assertThat(addressVm.countryId()).isEqualTo(100L);
    }

    @Test
    void testConstructor_withAllFields_shouldWork() {
        // When
        AddressVm addressVm = new AddressVm(
                2L, "Jane Smith", "987-654-3210", "456 Oak Avenue",
                "Metropolis", "54321", 2L, 20L, 200L);

        // Then
        assertThat(addressVm).isNotNull();
        assertThat(addressVm.id()).isEqualTo(2L);
        assertThat(addressVm.contactName()).isEqualTo("Jane Smith");
        assertThat(addressVm.phone()).isEqualTo("987-654-3210");
        assertThat(addressVm.addressLine1()).isEqualTo("456 Oak Avenue");
        assertThat(addressVm.city()).isEqualTo("Metropolis");
        assertThat(addressVm.zipCode()).isEqualTo("54321");
        assertThat(addressVm.districtId()).isEqualTo(2L);
        assertThat(addressVm.stateOrProvinceId()).isEqualTo(20L);
        assertThat(addressVm.countryId()).isEqualTo(200L);
    }

    @Test
    void testBuilder_withPartialFields_shouldCreateInstance() {
        // When
        AddressVm addressVm = AddressVm.builder()
                .id(3L)
                .contactName("Bob Brown")
                .build();

        // Then
        assertThat(addressVm).isNotNull();
        assertThat(addressVm.id()).isEqualTo(3L);
        assertThat(addressVm.contactName()).isEqualTo("Bob Brown");
        assertThat(addressVm.phone()).isNull();
        assertThat(addressVm.addressLine1()).isNull();
    }

    @Test
    void testConstructor_withNullValues_shouldAccept() {
        // When
        AddressVm addressVm = new AddressVm(null, null, null, null, null, null, null, null, null);

        // Then
        assertThat(addressVm).isNotNull();
        assertThat(addressVm.id()).isNull();
        assertThat(addressVm.contactName()).isNull();
        assertThat(addressVm.phone()).isNull();
    }

    @Test
    void testConstructor_withEmptyStrings_shouldWork() {
        // When
        AddressVm addressVm = new AddressVm(4L, "", "", "", "", "", 4L, 40L, 400L);

        // Then
        assertThat(addressVm).isNotNull();
        assertThat(addressVm.id()).isEqualTo(4L);
        assertThat(addressVm.contactName()).isEmpty();
        assertThat(addressVm.phone()).isEmpty();
    }

    @Test
    void testConstructor_withMinimalFields_shouldWork() {
        // When
        AddressVm addressVm = new AddressVm(5L, null, null, null, null, null, null, null, null);

        // Then
        assertThat(addressVm).isNotNull();
        assertThat(addressVm.id()).isEqualTo(5L);
        assertThat(addressVm.contactName()).isNull();
    }
}
