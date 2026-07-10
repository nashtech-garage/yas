package com.yas.backofficebff.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class AuthenticatedUserTest {

    @Test
    void testAuthenticatedUser_shouldCreateWithUsername() {
        AuthenticatedUser user = new AuthenticatedUser("testuser");
        assertNotNull(user);
        assertEquals("testuser", user.username());
    }

    @Test
    void testAuthenticatedUser_shouldReturnCorrectUsername() {
        String expectedUsername = "admin@yas.com";
        AuthenticatedUser user = new AuthenticatedUser(expectedUsername);
        assertEquals(expectedUsername, user.username());
    }
}
