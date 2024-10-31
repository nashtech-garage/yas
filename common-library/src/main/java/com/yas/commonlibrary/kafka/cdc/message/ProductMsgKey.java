package com.yas.commonlibrary.kafka.cdc.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@lombok.Getter
@lombok.Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductMsgKey {
    private Long id;
}
