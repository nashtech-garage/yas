package com.yas.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ProductResultDto is a Data Transfer Object (DTO) representing an enriched product result.
 * It extends {@link ProductDto} to include core product information, and adds fields for
 * associated brand, categories, and product attributes, providing a comprehensive view
 * of product details with related entities.
 * This class is annotated for JSON serialization, ensuring only non-null properties are
 * included and unknown properties are ignored during deserialization.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("java:S1068")
public class ProductResultDto extends ProductDto {
    private BrandDto brand;
    private Set<CategoryDto> categories = new HashSet<>();
    private Set<ProductAttributeDto> productAttributes = new HashSet<>();

}
