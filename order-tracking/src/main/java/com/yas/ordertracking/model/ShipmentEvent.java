package com.yas.ordertracking.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShipmentEvent {
    @NotBlank
    private String orderId;
    @NotBlank
    private String carrier;
    @NotBlank
    private String trackingNumber;
    @NotBlank
    private String status; // e.g., CREATED, PICKED_UP, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED
    @NotNull
    private OffsetDateTime occurredAt;
    private String location;
    private String details;
}


