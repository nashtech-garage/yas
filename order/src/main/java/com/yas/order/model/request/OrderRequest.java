package com.yas.order.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yas.order.model.enumeration.OrderStatus;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private ZonedDateTime createdFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private ZonedDateTime createdTo;

    @JsonProperty
    private String warehouse;

    @JsonProperty
    private String productName;

    @JsonProperty
    private List<OrderStatus> orderStatus;

    @JsonProperty
    private String billingPhoneNumber;

    @JsonProperty
    private String email;

    @JsonProperty
    private String billingCountry;

    @JsonProperty
    private int pageNo;

    @JsonProperty
    private int pageSize;
}
