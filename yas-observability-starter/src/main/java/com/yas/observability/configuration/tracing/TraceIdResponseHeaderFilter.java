package com.yas.observability.configuration.tracing;

import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jspecify.annotations.Nullable;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Servlet filter that adds the current trace ID to the response headers for each HTTP request.
 * This allows clients and downstream services to correlate requests with their trace information.
 *
 * Not a Spring-managed component directly — instantiated by {@link TraceIdServletConfiguration},
 * which carries all @Conditional guards so this class is never loaded on WebFlux classpaths.
 */
public class TraceIdResponseHeaderFilter extends OncePerRequestFilter {

    private final Tracer tracer;

    TraceIdResponseHeaderFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
        ServletException, IOException {
        String traceId = getTraceId();
        if (traceId != null) {
            response.setHeader("X-Trace-Id", traceId);
        }
        filterChain.doFilter(request, response);
    }

    private @Nullable String getTraceId() {
        TraceContext context = this.tracer.currentTraceContext().context();
        return context != null ? context.traceId() : null;
    }

}