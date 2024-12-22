package com.yas.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ProductDto is a Data Transfer Object (DTO) representing detailed information about a product.
 * It extends {@link BaseDto} to inherit entity properties like ID and name, along with metadata
 * fields. This class includes additional product-specific fields such as brand, price, and
 * publication status, making it useful for various product-related operations.
 * The class is annotated for JSON serialization, ensuring only non-null properties are included
 * and unknown properties are ignored during deserialization.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("java:S1068")
public class ProductDto extends BaseDto {
    @JsonProperty("brand_id")
    private Long brandId;
    @JsonProperty("short_description")
    private String shortDescription;
    private String specification;
    @JsonProperty("is_published")
    private Boolean published;
    @JsonProperty("meta_description")
    private String metaDescription;
    @JsonProperty("meta_title")
    private String metaTitle;
    @JsonProperty("meta_keyword")
    private String metaKeyword;
    private Double price;
    private String description;
    @JsonProperty("parent_id")
    private Long parentId;
}
