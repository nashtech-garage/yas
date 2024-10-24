package com.yas.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductResultDTO extends ProductDTO {
    private BrandDTO brand;
    private Set<CategoryDTO> categories = new HashSet<>();
    private Set<ProductAttributeDTO> productAttributes = new HashSet<>();

}
