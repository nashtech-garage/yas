package com.yas.backofficebff.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AuthenticatedUserTest {

    @Test
    void constructor_shouldSetUsername() {
        AuthenticatedUser user = new AuthenticatedUser("john_doe");
        assertThat(user.username()).isEqualTo("john_doe");
    }

    @Test
    void constructor_shouldAcceptNullUsername() {
        AuthenticatedUser user = new AuthenticatedUser(null);
        assertThat(user.username()).isNull();
    }

    @Test
    void record_equalsSameValues_shouldBeEqual() {
        AuthenticatedUser user1 = new AuthenticatedUser("alice");
        AuthenticatedUser user2 = new AuthenticatedUser("alice");
        assertThat(user1).isEqualTo(user2);
    }

    @Test
    void record_differentValues_shouldNotBeEqual() {
        AuthenticatedUser user1 = new AuthenticatedUser("alice");
        AuthenticatedUser user2 = new AuthenticatedUser("bob");
        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    void record_toString_shouldContainUsername() {
        AuthenticatedUser user = new AuthenticatedUser("testuser");
        assertThat(user.toString()).contains("testuser");
    }
}
