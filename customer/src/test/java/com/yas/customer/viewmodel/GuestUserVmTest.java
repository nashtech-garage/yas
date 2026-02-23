package com.yas.customer.viewmodel;

import com.yas.customer.viewmodel.customer.GuestUserVm;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GuestUserVmTest {

    @Test
    void testConstructor_shouldCreateInstance() {
        // When
        GuestUserVm guestUserVm = new GuestUserVm("guest-123", "guest@example.com", "guest-pass");

        // Then
        assertThat(guestUserVm).isNotNull();
        assertThat(guestUserVm.userId()).isEqualTo("guest-123");
        assertThat(guestUserVm.email()).isEqualTo("guest@example.com");
        assertThat(guestUserVm.password()).isEqualTo("guest-pass");
    }

    @Test
    void testConstructor_withNullValues_shouldAccept() {
        // When
        GuestUserVm guestUserVm = new GuestUserVm(null, null, null);

        // Then
        assertThat(guestUserVm).isNotNull();
        assertThat(guestUserVm.userId()).isNull();
        assertThat(guestUserVm.email()).isNull();
        assertThat(guestUserVm.password()).isNull();
    }

    @Test
    void testConstructor_withEmptyStrings_shouldWork() {
        // When
        GuestUserVm guestUserVm = new GuestUserVm("", "", "");

        // Then
        assertThat(guestUserVm).isNotNull();
        assertThat(guestUserVm.userId()).isEmpty();
        assertThat(guestUserVm.email()).isEmpty();
        assertThat(guestUserVm.password()).isEmpty();
    }
}
