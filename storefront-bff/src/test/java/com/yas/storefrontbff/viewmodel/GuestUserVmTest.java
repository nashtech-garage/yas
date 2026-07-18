package com.yas.storefrontbff.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class GuestUserVmTest {

    @Test
    void testGuestUserVm() {
        GuestUserVm user = new GuestUserVm("id", "email", "pass");
        assertEquals("id", user.userId());
        assertEquals("email", user.email());
        assertEquals("pass", user.password());
    }
}
