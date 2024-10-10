package com.yas.recommendation.kafka.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {

    private int id;
    private String createdBy;
    private String createdOn;
    private String lastModifiedBy;
    private String lastModifiedOn;
    private String description;
    private String gtin;
    private boolean hasOptions;
    private boolean isAllowedToOrder;
    private boolean isFeatured;
    private boolean isPublished;
    private boolean isVisibleIndividually;
    private String metaDescription;
    private String metaKeyword;
    private String metaTitle;
    private String name;
    private double price;
    private String shortDescription;
    private String sku;
    private String slug;
    private String specification;
    private int thumbnailMediaId;
    private int brandId;
    private Integer parentId;
    private boolean stockTrackingEnabled;
    private boolean taxIncluded;
    private int stockQuantity;
    private int taxClassId;
    private Dimension weight;
    private Dimension length;
    private Dimension width;
    private Dimension height;
    private String dimensionUnit;

}
