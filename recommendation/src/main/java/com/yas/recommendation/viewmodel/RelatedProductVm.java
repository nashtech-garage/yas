package com.yas.recommendation.viewmodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

@lombok.Setter
@lombok.Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelatedProductVm {

    @JsonProperty("id")
    private Long productId;

    private String name;

    private BigDecimal price;

    @JsonProperty("brandName")
    private String brand;

    @JsonProperty("metaTitle")
    private String title;

    private String description;

    private String metaDescription;

    private String specification;

    private ImageVm thumbnail;

    private List<ImageVm> productImages;

    private String slug;

    public RelatedProductVm() {
        // This default constructor.
    }
}

