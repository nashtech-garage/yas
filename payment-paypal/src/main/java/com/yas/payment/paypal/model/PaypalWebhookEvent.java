package com.yas.payment.paypal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yas.payment.paypal.model.enumeration.PaypalPaymentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaypalWebhookEvent {

    @NotNull
    private String id;

    @NotNull
    @JsonProperty("event_type")
    private PaypalPaymentStatus eventType;

    @NotNull
    @Valid
    private PaypalPaymentResource resource;
}
