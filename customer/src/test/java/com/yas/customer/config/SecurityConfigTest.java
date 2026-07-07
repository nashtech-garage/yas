package com.yas.customer.config;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityConfigTest {

    @Test
    void testJwtAuthenticationConverterForKeycloak() {
        SecurityConfig config = new SecurityConfig();
        JwtAuthenticationConverter converter = config.jwtAuthenticationConverterForKeycloak();
        
        // Reflection or getting the converter from private field isn't possible directly,
        // but we can just test the converter behavior since it's applied during runtime.
        // Actually we can't get the inner lambda converter without reflection,
        // wait, we can just call it via reflection if we really want, or just let Spring test do it.
        // But since we want coverage, let's call it by extracting it via reflection.
        
        try {
            java.lang.reflect.Field field = JwtAuthenticationConverter.class.getDeclaredField("jwtGrantedAuthoritiesConverter");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter =
                    (Converter<Jwt, Collection<GrantedAuthority>>) field.get(converter);

            Jwt jwt = mock(Jwt.class);
            Map<String, Collection<String>> realmAccess = Map.of("roles", List.of("ADMIN", "USER"));
            when(jwt.getClaim("realm_access")).thenReturn(realmAccess);

            Collection<GrantedAuthority> authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
            
            assertEquals(2, authorities.size());
            assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
            assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
            
        } catch (Exception e) {
            org.junit.jupiter.api.Assertions.fail("Reflection failed: " + e.getMessage());
        }
    }
}
