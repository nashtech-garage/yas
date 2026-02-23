package com.yas.customer.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserAddressTest {

    @Test
    void testBuilder_shouldCreateInstance() {
        // When
        UserAddress userAddress = UserAddress.builder()
                .id(1L)
                .userId("user123")
                .addressId(100L)
                .isActive(true)
                .build();

        // Then
        assertThat(userAddress).isNotNull();
        assertThat(userAddress.getId()).isEqualTo(1L);
        assertThat(userAddress.getUserId()).isEqualTo("user123");
        assertThat(userAddress.getAddressId()).isEqualTo(100L);
        assertThat(userAddress.getIsActive()).isTrue();
    }

    @Test
    void testSetters_shouldUpdateFields() {
        // Given
        UserAddress userAddress = new UserAddress();

        // When
        userAddress.setId(2L);
        userAddress.setUserId("user456");
        userAddress.setAddressId(200L);
        userAddress.setIsActive(false);

        // Then
        assertThat(userAddress.getId()).isEqualTo(2L);
        assertThat(userAddress.getUserId()).isEqualTo("user456");
        assertThat(userAddress.getAddressId()).isEqualTo(200L);
        assertThat(userAddress.getIsActive()).isFalse();
    }

    @Test
    void testAllArgsConstructor_shouldCreateInstance() {
        // When
        UserAddress userAddress = new UserAddress(3L, "user789", 300L, true);

        // Then
        assertThat(userAddress).isNotNull();
        assertThat(userAddress.getId()).isEqualTo(3L);
        assertThat(userAddress.getUserId()).isEqualTo("user789");
        assertThat(userAddress.getAddressId()).isEqualTo(300L);
        assertThat(userAddress.getIsActive()).isTrue();
    }

    @Test
    void testNoArgsConstructor_shouldCreateEmptyInstance() {
        // When
        UserAddress userAddress = new UserAddress();

        // Then
        assertThat(userAddress).isNotNull();
        assertThat(userAddress.getId()).isNull();
        assertThat(userAddress.getUserId()).isNull();
        assertThat(userAddress.getAddressId()).isNull();
        assertThat(userAddress.getIsActive()).isNull();
    }

    @Test
    void testIsActive_withNullValue_shouldHandleGracefully() {
        // Given
        UserAddress userAddress = UserAddress.builder()
                .id(4L)
                .userId("user000")
                .addressId(400L)
                .isActive(null)
                .build();

        // Then
        assertThat(userAddress.getIsActive()).isNull();
    }

    @Test
    void testIsActive_toggleValue_shouldUpdate() {
        // Given
        UserAddress userAddress = UserAddress.builder()
                .id(5L)
                .userId("user111")
                .addressId(500L)
                .isActive(false)
                .build();

        // When
        userAddress.setIsActive(true);

        // Then
        assertThat(userAddress.getIsActive()).isTrue();
    }

    @Test
    void testBuilder_withPartialFields_shouldCreateInstance() {
        // When
        UserAddress userAddress = UserAddress.builder()
                .userId("user222")
                .addressId(600L)
                .build();

        // Then
        assertThat(userAddress).isNotNull();
        assertThat(userAddress.getId()).isNull();
        assertThat(userAddress.getUserId()).isEqualTo("user222");
        assertThat(userAddress.getAddressId()).isEqualTo(600L);
        assertThat(userAddress.getIsActive()).isNull();
    }
}
