package com.yas.customer.viewmodel;

import com.yas.customer.viewmodel.address.AddressVm;
import com.yas.customer.model.UserAddress;
import com.yas.customer.viewmodel.useraddress.UserAddressVm;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserAddressVmTest {

    @Test
    void testFromModel_shouldMapCorrectly() {
        // Given
        AddressVm addressVm = new AddressVm(1L, "Contact", "123456", "Address", "City", "12345", 1L, 1L, 1L);
        UserAddress userAddress = UserAddress.builder()
                .id(1L)
                .userId("user123")
                .addressId(1L)
                .isActive(true)
                .build();

        // When
        UserAddressVm result = UserAddressVm.fromModel(userAddress, addressVm);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.userId()).isEqualTo("user123");
        assertThat(result.addressGetVm()).isEqualTo(addressVm);
        assertThat(result.isActive()).isTrue();
    }

    @Test
    void testFromModel_whenInactive_shouldMapCorrectly() {
        // Given
        AddressVm addressVm = new AddressVm(2L, "Contact", "123456", "Address", "City", "12345", 1L, 1L, 1L);
        UserAddress userAddress = UserAddress.builder()
                .id(2L)
                .userId("user456")
                .addressId(2L)
                .isActive(false)
                .build();

        // When
        UserAddressVm result = UserAddressVm.fromModel(userAddress, addressVm);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.userId()).isEqualTo("user456");
        assertThat(result.isActive()).isFalse();
    }

    @Test
    void testBuilder_shouldCreateInstance() {
        // Given
        AddressVm addressVm = new AddressVm(3L, "Contact", "123456", "Address", "City", "12345", 1L, 1L, 1L);

        // When
        UserAddressVm result = UserAddressVm.builder()
                .id(3L)
                .userId("user789")
                .addressGetVm(addressVm)
                .isActive(true)
                .build();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(3L);
        assertThat(result.userId()).isEqualTo("user789");
        assertThat(result.addressGetVm()).isEqualTo(addressVm);
        assertThat(result.isActive()).isTrue();
    }
}
