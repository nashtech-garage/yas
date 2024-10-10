package com.yas.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private Long id;

    @JsonProperty("brand_id")
    private Long brandId;

    private String name;

    @JsonProperty("short_description")
    private String shortDescription;

    private String specification;

    @JsonProperty("is_published")
    private boolean isPublished;

    @JsonProperty("meta_description")
    private String metaDescription;

    @JsonProperty("meta_title")
    private String metaTitle;

    @JsonProperty("meta_keyword")
    private String metaKeyword;

    private Double price;
}
