package com.yas.backofficebff.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class AuthenticatedUserTest {

    @Test
    void constructor_shouldSetUsername() {
        AuthenticatedUser user = new AuthenticatedUser("testuser");
        assertEquals("testuser", user.username());
    }

    @Test
    void equals_sameUsername_shouldBeEqual() {
        AuthenticatedUser user1 = new AuthenticatedUser("testuser");
        AuthenticatedUser user2 = new AuthenticatedUser("testuser");
        assertEquals(user1, user2);
    }

    @Test
    void equals_differentUsername_shouldNotBeEqual() {
        AuthenticatedUser user1 = new AuthenticatedUser("user1");
        AuthenticatedUser user2 = new AuthenticatedUser("user2");
        assertNotEquals(user1, user2);
    }

    @Test
    void hashCode_sameUsername_shouldBeEqual() {
        AuthenticatedUser user1 = new AuthenticatedUser("testuser");
        AuthenticatedUser user2 = new AuthenticatedUser("testuser");
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void toString_shouldContainUsername() {
        AuthenticatedUser user = new AuthenticatedUser("testuser");
        assertNotNull(user.toString());
        assertEquals("AuthenticatedUser[username=testuser]", user.toString());
    }
}
