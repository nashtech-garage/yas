package com.yas.sampledata.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SecurityConfigTest {

    @Test
    void testJwtAuthenticationConverterBean() {
        SecurityConfig config = new SecurityConfig();
        JwtAuthenticationConverter converter = config.jwtAuthenticationConverterForKeycloak();
        assertNotNull(converter);
    }
}
