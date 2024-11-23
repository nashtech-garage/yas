package com.yas.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ProductAttributeDto is a Data Transfer Object (DTO) representing an attribute associated with a product.
 * It extends {@link BaseDto} to inherit entity metadata and identification, and includes an additional
 * field for the attribute value. This class is annotated for JSON serialization, ensuring only non-null
 * properties are included and unknown properties are ignored during deserialization.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("java:S1068")
public class ProductAttributeDto extends BaseDto {
    private String value;

    public ProductAttributeDto(String op, Long ts, Long id, String name, String value) {
        super(op, ts, id, name);
        this.value = value;
    }
}
