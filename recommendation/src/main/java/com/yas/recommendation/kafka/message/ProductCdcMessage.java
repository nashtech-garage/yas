package com.yas.recommendation.kafka.message;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCdcMessage {

    private Product after;

    @NotNull
    private Product before;

    private String op;

}
