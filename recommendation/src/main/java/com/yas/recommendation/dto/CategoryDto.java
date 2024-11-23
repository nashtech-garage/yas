package com.yas.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * CategoryDto is a Data Transfer Object (DTO) that represents category information.
 * It extends {@link BaseDto} to inherit basic entity properties like ID and name,
 * along with metadata fields for tracking changes. This class is annotated for JSON
 * serialization, ensuring only non-null properties are included and unknown properties
 * are ignored during deserialization.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
public class CategoryDto extends BaseDto {
    public CategoryDto(String op, Long ts, Long id, String name) {
        super(op, ts, id, name);
    }
}
