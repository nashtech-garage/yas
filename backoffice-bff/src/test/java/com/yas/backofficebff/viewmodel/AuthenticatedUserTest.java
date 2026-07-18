package com.yas.backofficebff.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AuthenticatedUserTest {

    @Test
    void testAuthenticatedUser() {
        AuthenticatedUser user = new AuthenticatedUser("admin");
        assertEquals("admin", user.username());
    }
}
