package com.yas.storefrontbff.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AuthenticatedUserVmTest {

    @Test
    void testAuthenticatedUserVm() {
        AuthenticatedUserVm user = new AuthenticatedUserVm("user");
        assertEquals("user", user.username());
    }
}
