package com.yas.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
public class ProductAttributeAggregationDTO {
    private Long productId;
    private Set<ProductAttributeDTO> productAttributes;

    public ProductAttributeAggregationDTO() {
        productAttributes = new HashSet<>();
    }

    public void add(ProductAttributeDTO productAttributeDTO) {
        productAttributes.add(productAttributeDTO);
    }
}
