package com.yas.recommendation.viewmodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelatedProductVm {

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

    private String specification;

    private ImageVm thumbnail;

    private List<ImageVm> productImages;

    public RelatedProductVm() {
    }
}

