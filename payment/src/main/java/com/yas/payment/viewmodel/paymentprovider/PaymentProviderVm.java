package com.yas.payment.viewmodel.paymentprovider;

import com.fasterxml.jackson.annotation.JsonInclude;

@lombok.Getter
@lombok.Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentProviderVm {

    private String id;
    private String name;
    private String configureUrl;
    private int version;
    private Long mediaId;
    private String iconUrl;

    public PaymentProviderVm(String id, String name, String configureUrl, int version, Long mediaId, String iconUrl) {
        this.id = id;
        this.name = name;
        this.configureUrl = configureUrl;
        this.version = version;
        this.mediaId = mediaId;
        this.iconUrl = iconUrl;
    }
}