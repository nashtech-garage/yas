package com.yas.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelatedProductDto {

    @JsonProperty("id")
    private Integer productId;

    @JsonProperty("meta_title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("meta_description")
    private String metaDescription;

    @JsonProperty("specification")
    private String specification;

    private ImageDto thumbnail;

    private List<ImageDto> productImages;

    public RelatedProductDto() {
    }
}

