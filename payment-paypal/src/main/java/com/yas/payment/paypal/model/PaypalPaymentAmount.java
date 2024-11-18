package com.yas.payment.paypal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class PaypalPaymentAmount {
    @SuppressWarnings("unused")
    private String currencyCode;
    @SuppressWarnings("unused")
    private String value;
}
