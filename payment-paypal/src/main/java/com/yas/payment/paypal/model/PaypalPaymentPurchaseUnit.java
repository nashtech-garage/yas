package com.yas.payment.paypal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PaypalPaymentPurchaseUnit {

    @JsonProperty("reference_id")
    private String referenceId;
    @SuppressWarnings("unused")
    private PaypalPaymentAmount amount;
}
