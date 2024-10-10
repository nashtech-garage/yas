package com.yas.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
public class ProductCategoryAggregationDTO {
    private Long productId;
    private Set<CategoryDTO> categories;

    public ProductCategoryAggregationDTO() {
        categories = new HashSet<>();
    }

    public void add(CategoryDTO categoryDTO) {
        categories.add(categoryDTO);
    }
}
