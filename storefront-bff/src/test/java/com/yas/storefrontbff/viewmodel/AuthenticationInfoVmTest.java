package com.yas.storefrontbff.viewmodel;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationInfoVmTest {

    @Test
    void constructor_shouldStoreAuthenticatedTrueAndUser() {
        AuthenticatedUserVm user = new AuthenticatedUserVm("alice");
        AuthenticationInfoVm info = new AuthenticationInfoVm(true, user);

        assertThat(info.isAuthenticated()).isTrue();
        assertThat(info.authenticatedUser()).isEqualTo(user);
    }

    @Test
    void constructor_shouldStoreAuthenticatedFalseAndNullUser() {
        AuthenticationInfoVm info = new AuthenticationInfoVm(false, null);

        assertThat(info.isAuthenticated()).isFalse();
        assertThat(info.authenticatedUser()).isNull();
    }

    @Test
    void equals_shouldReturnTrueForSameValues() {
        AuthenticatedUserVm user = new AuthenticatedUserVm("alice");
        assertThat(new AuthenticationInfoVm(true, user))
                .isEqualTo(new AuthenticationInfoVm(true, user));
    }

    @Test
    void equals_shouldReturnFalseWhenAuthenticatedDiffers() {
        AuthenticatedUserVm user = new AuthenticatedUserVm("alice");
        assertThat(new AuthenticationInfoVm(true, user))
                .isNotEqualTo(new AuthenticationInfoVm(false, user));
    }

    @Test
    void equals_shouldReturnFalseWhenUserDiffers() {
        assertThat(new AuthenticationInfoVm(true, new AuthenticatedUserVm("alice")))
                .isNotEqualTo(new AuthenticationInfoVm(true, new AuthenticatedUserVm("bob")));
    }

    @Test
    void hashCode_shouldBeConsistentForSameValues() {
        AuthenticatedUserVm user = new AuthenticatedUserVm("alice");
        assertThat(new AuthenticationInfoVm(true, user).hashCode())
                .isEqualTo(new AuthenticationInfoVm(true, user).hashCode());
    }

    @Test
    void toString_shouldContainFields() {
        AuthenticationInfoVm info = new AuthenticationInfoVm(true, new AuthenticatedUserVm("alice"));
        assertThat(info.toString()).contains("true");
    }
}
