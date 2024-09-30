package com.yas.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelatedProductDto {

    @JsonProperty("id")
    private Integer productId;

    private String name;

    private BigDecimal price;

    @JsonProperty("brandName")
    private String brand;

    @JsonProperty("metaTitle")
    private String title;

    private String description;

    private String metaDescription;

    @JsonProperty("specification")
    private String specification;

    private ImageDto thumbnail;

    private List<ImageDto> productImages;

    public RelatedProductDto() {
    }
}

