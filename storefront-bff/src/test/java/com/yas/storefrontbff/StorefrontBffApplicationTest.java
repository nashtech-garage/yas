package com.yas.storefrontbff;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class StorefrontBffApplicationTest {

    @Test
    void writeableHeaders_FilterMutatesRequestAndPreservesHeaders() {
        StorefrontBffApplication application = new StorefrontBffApplication();
        WebFilter webFilter = application.writeableHeaders();

        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test")
                .header("X-Test", "value")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        AtomicReference<ServerWebExchange> capturedExchange = new AtomicReference<>();
        WebFilterChain chain = e -> {
            capturedExchange.set(e);
            // Accessing headers ensures ServerHttpRequestDecorator#getHeaders is executed.
            HttpHeaders headers = e.getRequest().getHeaders();
            assertThat(headers.getFirst("X-Test")).isEqualTo("value");
            headers.set("X-Added", "added");
            return Mono.empty();
        };

        webFilter.filter(exchange, chain).block();

        assertThat(capturedExchange.get()).isNotNull();
        assertThat(capturedExchange.get().getRequest().getHeaders().getFirst("X-Added")).isEqualTo("added");
    }
}
