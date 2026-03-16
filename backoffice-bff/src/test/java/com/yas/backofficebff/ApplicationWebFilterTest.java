package com.yas.backofficebff;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import reactor.test.StepVerifier;

class ApplicationWebFilterTest {

    private final Application application = new Application();

    @Test
    void writeableHeaders_shouldCreateWebFilterBean() {
        WebFilter filter = application.writeableHeaders();
        assertThat(filter).isNotNull();
    }

    @Test
    void writeableHeaders_shouldPassExchangeToChain() {
        WebFilter filter = application.writeableHeaders();

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .header("X-Custom-Header", "value")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        WebFilterChain chain = mock(WebFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }

    @Test
    void writeableHeaders_decoratedRequestShouldHaveWriteableHeaders() {
        WebFilter filter = application.writeableHeaders();

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // Capture the exchange passed to the chain
        WebFilterChain chain = filteredExchange -> {
            // Headers on the decorated request must be writable (no UnsupportedOperationException)
            HttpHeaders headers = filteredExchange.getRequest().getHeaders();
            // Adding a header should not throw
            try {
                headers.add("X-Added-By-Filter", "test");
            } catch (UnsupportedOperationException e) {
                // If this throws it means headers are NOT writeable — the filter is broken
                return Mono.error(new AssertionError("Headers should be writeable", e));
            }
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }
}
