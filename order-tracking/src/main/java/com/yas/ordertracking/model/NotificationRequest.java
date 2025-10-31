package com.yas.ordertracking.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationRequest {
    @NotBlank
    private String orderId;
    @Email
    private String email;
    private String phone;
    @NotBlank
    private String template;
}


