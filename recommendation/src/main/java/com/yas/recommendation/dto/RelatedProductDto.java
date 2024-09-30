package com.yas.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelatedProductDto {

    @JsonProperty("id")
    private Integer productId;

    @JsonProperty("meta_title")
    private String title;

    private String description;

    @JsonProperty("meta_description")
    private String metaDescription;

    private String specification;

    public RelatedProductDto() {
    }
}
