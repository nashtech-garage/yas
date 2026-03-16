package com.yas.storefrontbff.viewmodel;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticatedUserVmTest {

    @Test
    void constructor_shouldStoreUsername() {
        AuthenticatedUserVm vm = new AuthenticatedUserVm("john_doe");
        assertThat(vm.username()).isEqualTo("john_doe");
    }

    @Test
    void constructor_shouldAllowNullUsername() {
        AuthenticatedUserVm vm = new AuthenticatedUserVm(null);
        assertThat(vm.username()).isNull();
    }

    @Test
    void constructor_shouldAllowEmptyUsername() {
        AuthenticatedUserVm vm = new AuthenticatedUserVm("");
        assertThat(vm.username()).isEmpty();
    }

    @Test
    void equals_shouldReturnTrueForSameUsername() {
        assertThat(new AuthenticatedUserVm("alice")).isEqualTo(new AuthenticatedUserVm("alice"));
    }

    @Test
    void equals_shouldReturnFalseForDifferentUsername() {
        assertThat(new AuthenticatedUserVm("alice")).isNotEqualTo(new AuthenticatedUserVm("bob"));
    }

    @Test
    void hashCode_shouldBeConsistentForSameUsername() {
        assertThat(new AuthenticatedUserVm("alice").hashCode())
                .isEqualTo(new AuthenticatedUserVm("alice").hashCode());
    }

    @Test
    void toString_shouldContainUsername() {
        assertThat(new AuthenticatedUserVm("alice").toString()).contains("alice");
    }
}