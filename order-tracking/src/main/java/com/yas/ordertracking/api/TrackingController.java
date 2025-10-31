package com.yas.ordertracking.api;

import com.yas.ordertracking.model.NotificationRequest;
import com.yas.ordertracking.model.ShipmentEvent;
import com.yas.ordertracking.model.TimelineEntry;
import com.yas.ordertracking.service.TrackingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@Validated
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService trackingService;

    @PostMapping("/events/shipment")
    @Operation(summary = "Ingest a shipment event from carrier/webhook")
    public ResponseEntity<Void> ingestShipmentEvent(@Valid @RequestBody ShipmentEvent event) {
        trackingService.ingestShipmentEvent(event);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/timeline/{orderId}")
    @Operation(summary = "Get order shipment timeline by orderId")
    public ResponseEntity<List<TimelineEntry>> getTimeline(@PathVariable @NotBlank String orderId) {
        return ResponseEntity.ok(trackingService.getTimeline(orderId));
    }

    @PostMapping("/notify")
    @Operation(summary = "Dispatch a customer notification for an order event")
    public ResponseEntity<Void> notifyCustomer(@Valid @RequestBody NotificationRequest request) {
        trackingService.dispatchNotification(request);
        return ResponseEntity.accepted().build();
    }
}


