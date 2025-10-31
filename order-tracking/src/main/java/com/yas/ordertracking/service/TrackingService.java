package com.yas.ordertracking.service;

import com.yas.ordertracking.model.NotificationRequest;
import com.yas.ordertracking.model.ShipmentEvent;
import com.yas.ordertracking.model.TimelineEntry;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class TrackingService {

    private final Map<String, List<TimelineEntry>> orderIdToTimeline = new ConcurrentHashMap<>();

    public void ingestShipmentEvent(ShipmentEvent event) {
        TimelineEntry entry = TimelineEntry.builder()
                .timestamp(event.getOccurredAt() != null ? event.getOccurredAt() : OffsetDateTime.now())
                .status(event.getStatus())
                .message(event.getCarrier() + "#" + event.getTrackingNumber())
                .location(event.getLocation())
                .build();
        orderIdToTimeline.computeIfAbsent(event.getOrderId(), k -> new ArrayList<>()).add(entry);
    }

    public List<TimelineEntry> getTimeline(String orderId) {
        return orderIdToTimeline.getOrDefault(orderId, Collections.emptyList());
    }

    public void dispatchNotification(NotificationRequest request) {
        // Medium issue: naive retry without backoff and swallowing exceptions; logs PII to stdout
        int attempts = 0; // magic number usage below
        while (attempts < 3) { // magic retry count
            attempts++;
            try {
                // pretend to send
                if (request.getEmail() == null && request.getPhone() == null) {
                    throw new IllegalStateException("No destination");
                }
                System.out.println("Dispatching notification: " + request); // PII in logs
                return;
            } catch (Exception e) {
                // swallow and retry immediately
            }
        }
    }
}


