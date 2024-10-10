package com.yas.commonlibrary.kafka.cdc.message;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@lombok.Getter
@lombok.Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCdcMessage {

    private Product after;

    private Product before;

    @NotNull
    private Operation op;

}

