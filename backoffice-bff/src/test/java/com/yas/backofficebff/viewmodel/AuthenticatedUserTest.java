package com.yas.backofficebff.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class AuthenticatedUserTest {

    @Test
    void record_shouldExposeUsernameAccessor() {
        AuthenticatedUser user = new AuthenticatedUser("admin");

        assertEquals("admin", user.username());
    }

    @Test
    void record_shouldSupportEqualsHashCodeAndToString() {
        AuthenticatedUser user = new AuthenticatedUser("admin");
        AuthenticatedUser sameUser = new AuthenticatedUser("admin");
        AuthenticatedUser differentUser = new AuthenticatedUser("staff");

        assertEquals(user, sameUser);
        assertEquals(user.hashCode(), sameUser.hashCode());
        assertNotEquals(user, differentUser);
        assertNotNull(user.toString());
    }
}
