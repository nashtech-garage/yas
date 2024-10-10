package com.yas.commonlibrary.kafka.cdc.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@lombok.Getter
@lombok.Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private long id;

    @JsonProperty("is_published")
    private boolean isPublished;

}
