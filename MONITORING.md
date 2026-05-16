# 📊 BFF Monitoring Implementation Guide

## Overview

This feature branch implements comprehensive monitoring for the YAS BFF (Backend for Frontend) services using OpenTelemetry, Prometheus, and Grafana.

## Components Added

### 1. **Java Monitoring Classes**

#### `MetricsCollector.java`
- Tracks API request metrics
- Records latency percentiles (p50, p95, p99)
- Monitors error rates
- Tracks cache hit/miss ratios
- Records downstream service latency

**Key Metrics:**
```
bff.requests.total       - Total API requests
bff.requests.errors      - Error count
bff.request.latency      - Request latency (ms)
bff.downstream.latency   - Downstream service latency
bff.cache.hits          - Cache hit count
bff.cache.misses        - Cache miss count
```

#### `MetricsWebFilter.java`
- Web filter to intercept all requests
- Automatically records metrics for each API call
- Logs high-latency requests (> 1s)
- Alerts on server errors (5xx status)

#### `BFFHealthIndicator.java`
- Custom health checks for downstream services
- Monitors service dependencies
- Provides status via `/actuator/health` endpoint
- Includes service connectivity information

### 2. **Grafana Dashboard**

**File:** `docker/grafana/provisioning/dashboards/bff-overview.json`

**Visualizations:**
- 📈 Request Rate (req/s)
- ⏱️ Request Latency Percentiles (p50, p95, p99)
- 🚨 Error Rate
- 💾 Cache Hit Ratio

**Access:** http://grafana/d/bff-overview

### 3. **Prometheus Alerts**

**File:** `docker/prometheus/alerts.yml`

**Critical Alerts:**
| Alert | Threshold | Duration |
|-------|-----------|----------|
| High Error Rate | > 5% | 5 minutes |
| High Latency (p95) | > 1 second | 5 minutes |
| Service Down | up == 0 | 1 minute |
| High Request Rate | > 1000 req/s | 5 minutes |
| Low Cache Ratio | < 50% | 10 minutes |

## Implementation Steps

### Step 1: Deploy to Docker

```bash
# Ensure alerts are loaded
docker compose -f docker-compose.o11y.yml up -d prometheus
```

### Step 2: Verify Metrics

```bash
# Check Prometheus is scraping metrics
curl http://localhost:9090/api/v1/targets

# Query metrics
curl "http://localhost:9090/api/v1/query?query=bff_requests_total"
```

### Step 3: Access Dashboards

1. **Grafana**: http://grafana/d/bff-overview
2. **Prometheus**: http://localhost:9090/graph
3. **Alerts**: http://localhost:9090/alerts

### Step 4: Configure Alerting

For production, configure alertmanager:

```yaml
# docker/prometheus/alertmanager.yml
global:
  resolve_timeout: 5m

route:
  receiver: 'default'
  group_by: ['alertname', 'severity']

receivers:
  - name: 'default'
    # Add your notification channels (email, Slack, etc.)
```

## Development

### Adding New Metrics

In `MetricsCollector.java`:

```java
private final Counter customCounter = Counter.builder("bff.custom.metric")
    .description("Custom metric description")
    .tag("service", "backoffice-bff")
    .register(meterRegistry);

public void recordCustomMetric() {
    customCounter.increment();
}
```

### Creating New Dashboards

1. Design in Grafana UI
2. Export JSON: Panel menu → More → Export
3. Save to: `docker/grafana/provisioning/dashboards/`
4. Restart Grafana for auto-provisioning

### Adding New Alerts

Edit `docker/prometheus/alerts.yml`:

```yaml
- alert: CustomAlert
  expr: | 
    custom_metric > threshold
  for: 5m
  labels:
    severity: warning
  annotations:
    summary: "Custom alert triggered"
```

## Testing

### Local Testing

```bash
# Start observability stack
docker compose -f docker-compose.o11y.yml up

# Start BFF service
cd backoffice-bff
mvn spring-boot:run

# Generate traffic
curl -i http://localhost:8087/api/products

# View metrics
curl http://localhost:8087/actuator/metrics

# View health
curl http://localhost:8087/actuator/health
```

### Verify Integration

1. Access Prometheus: http://localhost:9090
2. Query: `bff_requests_total`
3. Check Grafana dashboard updates

## Performance Considerations

- **Metrics Collection**: Minimal overhead (< 1ms per request)
- **Storage**: Prometheus retention configured for 1 hour
- **Scrape Interval**: 2 seconds (configurable in prometheus.yml)

## Troubleshooting

### Metrics not appearing

```bash
# Check filter chain
curl -v http://localhost:8087/api/test

# Verify metrics endpoint
curl http://localhost:8087/actuator/metrics/bff.requests.total

# Check Prometheus scrape targets
curl http://localhost:9090/api/v1/targets
```

### High memory usage

```yaml
# Reduce retention in docker-compose.o11y.yml
command: [
  "--storage.tsdb.retention.time=30m"  # Reduce from 1h
]
```

### Dashboard not loading

1. Ensure Grafana datasource is configured
2. Check prometheus.yml job configuration
3. Restart Grafana: `docker compose restart grafana`

## Next Steps

1. **Alerting**: Configure Slack/Email notifications
2. **Distributed Tracing**: Integrate Tempo for trace analysis
3. **Logging**: Centralize logs with Loki
4. **SLOs**: Define Service Level Objectives
5. **Custom Dashboards**: Create business KPI dashboards

## References

- [OpenTelemetry Documentation](https://opentelemetry.io/)
- [Prometheus Alerting](https://prometheus.io/docs/prometheus/latest/configuration/alerting_rules/)
- [Grafana Dashboard Provisioning](https://grafana.com/docs/grafana/latest/dashboards/manage-dashboards/)
- [Micrometer Documentation](https://micrometer.io/)

## PR Checklist

- [x] Created Java monitoring classes
- [x] Implemented web filter for request interception
- [x] Created custom health indicator
- [x] Added Grafana dashboard
- [x] Configured Prometheus alerts
- [x] Updated docker-compose.o11y.yml
- [x] Added documentation

---

**Branch:** `feature/monitoring`  
**Related Issue:** #monitoring-improvement  
**Type:** Feature  
**Scope:** Observability Enhancement
