# yas-observability-starter

`yas-observability-starter` provides **standardized observability for
all YAS microservices** using **Spring Boot 4 native OpenTelemetry
integration**.

It automatically configures **tracing, metrics, and log correlation**
and exports them to an **OpenTelemetry Collector**.

This starter replaces the previous **OpenTelemetry Java Agent
(`-javaagent`) approach**.

## Benefits

-   No Java agent required
-   Fully compatible with **Spring Boot 4**
-   Works with **GraalVM / AOT**
-   Centralized configuration for all services
-   Consistent **trace, metrics, and log correlation**

---

# What the Starter Provides

When added to a service, the starter automatically enables the following
features.

## Distributed Tracing

-   Uses **Spring Boot 4 OpenTelemetry integration**
-   Exports traces via **OTLP** to the OpenTelemetry Collector

## Metrics

-   HTTP server metrics
-   JVM metrics
-   Percentile histograms for HTTP requests

## Log Correlation

Logs are exported using the **OpenTelemetry Logback Appender**.

All logs automatically include:

-   `traceId`
-   `spanId`

This allows correlating logs with traces in the observability stack.

## HTTP Trace ID Header

Each HTTP response includes:

    X-Trace-Id

This allows clients to correlate API responses with traces.

## Async Context Propagation

Trace context automatically propagates across:

-   `@Async`
-   Thread pools
-   Background tasks

---

# Add the Starter

Add the dependency to any YAS service.

``` xml
<dependency>
    <groupId>com.yas</groupId>
    <artifactId>yas-observability-starter</artifactId>
</dependency>
```

No additional configuration is required.

---

# Logback Configuration

Include the shared Logback fragment in your `logback-spring.xml`.

``` xml
    <!-- OTel log exporter: sends log records via OTLP -->
    <appender name="OTEL"
        class="io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender">
    </appender>

	<root level="INFO">
	    <appender-ref ref="OTEL" />
	</root>
```

This enables exporting logs via OpenTelemetry.

---

# Default Configuration

The starter provides default observability settings.

``` yaml
management:
  tracing:
    sampling:
      probability: 0.1

  metrics:
    distribution:
      percentiles-histogram:
        http:
          server:
            requests: true
```

## Default OTLP Endpoints

    Traces / Logs (gRPC)
    http://collector:5555

    Metrics (HTTP)
    http://collector:6666/v1/metrics

Services can override any property using standard Spring configuration.

---

# Configuration Properties

## Disable observability entirely

``` yaml
yas:
  observability:
    enabled: false
```