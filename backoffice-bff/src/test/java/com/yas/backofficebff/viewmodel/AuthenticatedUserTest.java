package com.yas.backofficebff.viewmodel;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticatedUserTest {

    @Test
    void constructor_shouldStoreUsername() {
        AuthenticatedUser user = new AuthenticatedUser("john_doe");
        assertThat(user.username()).isEqualTo("john_doe");
    }

    @Test
    void constructor_shouldAllowNullUsername() {
        AuthenticatedUser user = new AuthenticatedUser(null);
        assertThat(user.username()).isNull();
    }

    @Test
    void constructor_shouldAllowEmptyUsername() {
        AuthenticatedUser user = new AuthenticatedUser("");
        assertThat(user.username()).isEmpty();
    }

    @Test
    void equals_shouldReturnTrueForSameUsername() {
        AuthenticatedUser user1 = new AuthenticatedUser("alice");
        AuthenticatedUser user2 = new AuthenticatedUser("alice");
        assertThat(user1).isEqualTo(user2);
    }

    @Test
    void equals_shouldReturnFalseForDifferentUsername() {
        AuthenticatedUser user1 = new AuthenticatedUser("alice");
        AuthenticatedUser user2 = new AuthenticatedUser("bob");
        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    void hashCode_shouldBeConsistentForSameUsername() {
        AuthenticatedUser user1 = new AuthenticatedUser("alice");
        AuthenticatedUser user2 = new AuthenticatedUser("alice");
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    void toString_shouldContainUsername() {
        AuthenticatedUser user = new AuthenticatedUser("alice");
        assertThat(user.toString()).contains("alice");
    }
}