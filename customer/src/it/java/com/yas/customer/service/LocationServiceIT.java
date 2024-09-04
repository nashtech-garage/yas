package com.yas.customer.service;

import static com.yas.customer.constant.TestConstants.CIRCUIT_BREAKER_NAME;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.yas.customer.viewmodel.address.AddressPostVm;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class LocationServiceIT {
    @Autowired
    private LocationService locationService;
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16").withReuse(true);

    @Test
    void test_getAddressesByIdList_shouldThrowCallNotPermittedException_whenCircuitBreakerIsOpen() {
        circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME).transitionToOpenState();
        List<Long> addressIds = Collections.singletonList(1L);
        assertThrows(CallNotPermittedException.class, () -> locationService.getAddressesByIdList(addressIds));
    }

    @Test
    void test_getAddressById_shouldThrowCallNotPermittedException_whenCircuitBreakerIsOpen() {
        circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME).transitionToOpenState();
        assertThrows(CallNotPermittedException.class, () -> locationService.getAddressById(1L));
    }

    @Test
    void test_createAddress_shouldThrowCallNotPermittedException_whenCircuitBreakerIsOpen() {
        circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME).transitionToOpenState();
        AddressPostVm addressPostVm =
            new AddressPostVm("my contact", "phone number", "address line 1", "city", "zip code", 1L, 1L, 1L);
        assertThrows(CallNotPermittedException.class, () -> locationService.createAddress(addressPostVm));
    }
}
