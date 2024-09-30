package com.yas.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.Map;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductVariationDTO(
        Long id,
        String name,
        String slug,
        String sku,
        String gtin,
        Double price,
        Map<Long, String> options
) {
}
