package com.yas.payment.viewmodel.paymentprovider;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

@lombok.Getter
@lombok.Setter
public class PaymentProviderReqVm {

    @NotNull
    private String id;

    @JsonProperty("isEnabled")
    private boolean isEnabled;

    @NotNull
    private String name;

    @NotNull
    private String configureUrl;

    private String landingViewComponentName;

    private String additionalSettings;

    private Long mediaId;

}