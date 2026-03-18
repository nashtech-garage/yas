package com.yas.payment.viewmodel.paymentprovider;

import com.fasterxml.jackson.annotation.JsonInclude;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentProviderVm {

    private String id;
    private String name;
    private String configureUrl;
    private int version;
    private Long mediaId;
    private String iconUrl;
}