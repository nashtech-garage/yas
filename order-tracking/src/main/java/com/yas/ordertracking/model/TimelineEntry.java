package com.yas.ordertracking.model;

import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimelineEntry {
    private OffsetDateTime timestamp;
    private String status;
    private String message;
    private String location;
}


