package com.yas.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ProductAttributeValueDto is a Data Transfer Object (DTO) representing the value of a specific attribute
 * associated with a product. It extends {@link BaseMetaDataDto} to include metadata fields such as
 * operation type and timestamp, and it provides additional fields to store attribute-specific information.
 * This class is annotated for JSON serialization, ensuring only non-null properties are included and
 * unknown properties are ignored during deserialization.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("java:S1068")
public class ProductAttributeValueDto extends BaseMetaDataDto {
    private Long id;
    private String value;
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("product_attribute_id")
    private Long productAttributeId;
    private String productAttributeName;
    private boolean isDeleted;

    public ProductAttributeValueDto(Long id) {
        this.id = id;
    }
}
