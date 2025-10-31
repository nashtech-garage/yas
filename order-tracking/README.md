# Order Tracking Service

Exposes APIs to ingest shipment events, query an order's shipment timeline, and dispatch customer notifications.

APIs:

- POST /v1/events/shipment
- GET /v1/timeline/{orderId}
- POST /v1/notify

Run locally:

```
mvn -pl order-tracking spring-boot:run
```


