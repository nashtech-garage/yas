package com.yas.storefrontbff.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;

class SecurityConfigTest {

    @Test
    void generateAuthoritiesFromClaim_prefixesWithROLE() {
        ReactiveClientRegistrationRepository repo = Mockito.mock(ReactiveClientRegistrationRepository.class);
        SecurityConfig config = new SecurityConfig(repo);

        var authorities = config.generateAuthoritiesFromClaim(List.of("admin", "customer"));

        assertThat(authorities)
            .extracting(a -> a.getAuthority())
            .containsExactlyInAnyOrder("ROLE_admin", "ROLE_customer");
    }
}
