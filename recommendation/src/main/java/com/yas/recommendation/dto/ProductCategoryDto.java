package com.yas.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ProductCategoryDto is a Data Transfer Object (DTO) representing the association between
 * a product and a category. It extends {@link BaseMetaDataDto} to include metadata fields
 * such as operation type and timestamp, and it provides fields for category and product
 * association details, including deletion status.
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
public class ProductCategoryDto extends BaseMetaDataDto {
    private Long id;
    @JsonProperty("category_id")
    private Long categoryId;
    @JsonProperty("product_id")
    private Long productId;
    private String categoryName;
    private boolean isDeleted;

    public ProductCategoryDto(Long id) {
        this.id = id;
    }
}
