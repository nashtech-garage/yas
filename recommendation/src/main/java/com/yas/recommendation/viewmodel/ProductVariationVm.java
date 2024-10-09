package com.yas.recommendation.viewmodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Map;

/**
 * A record that represents the variations of a product.
 * */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductVariationVm(
        Long id,
        String name,
        String slug,
        String sku,
        String gtin,
        Double price,
        Map<Long, String> options
) {
}
