package com.yas.media.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private SecurityFilterChain filterChain;

    @Test
    void testSecurityConfigCreation() {
        assertNotNull(securityConfig);
    }

    @Test
    void testSecurityFilterChainBean() {
        assertNotNull(filterChain);
    }

    @Test
    void testJwtAuthenticationConverterBean() {
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverterForKeycloak();
        assertNotNull(converter);
    }

    @Test
    void testSecurityFilterChainNotNull() {
        assertNotNull(filterChain);
    }

    @Test
    void testMultipleSecurityBeans() {
        JwtAuthenticationConverter converter1 = securityConfig.jwtAuthenticationConverterForKeycloak();
        JwtAuthenticationConverter converter2 = securityConfig.jwtAuthenticationConverterForKeycloak();

        assertNotNull(converter1);
        assertNotNull(converter2);
    }

    @Test
    void testSecurityConfigBeanWiring() {
        assertNotNull(securityConfig);
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverterForKeycloak();
        assertNotNull(converter);
    }
}
