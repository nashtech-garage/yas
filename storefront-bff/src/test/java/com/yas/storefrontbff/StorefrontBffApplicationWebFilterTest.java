package com.yas.storefrontbff;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StorefrontBffApplicationWebFilterTest {

    private final StorefrontBffApplication application = new StorefrontBffApplication();

    @Test
    void writeableHeaders_shouldCreateNonNullWebFilter() {
        WebFilter filter = application.writeableHeaders();
        assertThat(filter).isNotNull();
    }

    @Test
    void writeableHeaders_filterShouldDelegateToChain() {
        WebFilter filter = application.writeableHeaders();

        MockServerHttpRequest request = MockServerHttpRequest.get("/storefront").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        WebFilterChain chain = mock(WebFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }

    @Test
    void writeableHeaders_decoratedRequestHeadersShouldBeWriteable() {
        WebFilter filter = application.writeableHeaders();

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .header("Accept", "application/json")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        WebFilterChain chain = filteredExchange -> {
            HttpHeaders headers = filteredExchange.getRequest().getHeaders();
            try {
                headers.add("X-Test-Header", "value");
            } catch (UnsupportedOperationException e) {
                return Mono.error(new AssertionError("Headers must be writeable after filter", e));
            }
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }

    @Test
    void writeableHeaders_originalHeadersShouldBePreserved() {
        WebFilter filter = application.writeableHeaders();

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .header("X-Original", "original-value")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        WebFilterChain chain = filteredExchange -> {
            HttpHeaders headers = filteredExchange.getRequest().getHeaders();
            assertThat(headers.getFirst("X-Original")).isEqualTo("original-value");
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }

    @Test
    void writeableHeaders_filterShouldHandleEmptyHeaders() {
        WebFilter filter = application.writeableHeaders();

        MockServerHttpRequest request = MockServerHttpRequest.get("/empty").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        WebFilterChain chain = mock(WebFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();
    }
}