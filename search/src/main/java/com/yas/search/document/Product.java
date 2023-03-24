package com.yas.search.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "product")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseDocument {
    @Id
    private Long id;

    private String name;

//    private String shortDescription;
//
//    private String description;
//
//    private String specification;
//
//    private String sku;
//
//    private String gtin;
//
//    private String slug;
//
//    private Double price;
//
//    private Boolean hasOptions;
//
//    private Boolean isAllowedToOrder;
//
//    private Boolean isPublished;
//
//    private Boolean isFeatured;
//
//    private Boolean isVisibleIndividually;
//
//    private Boolean stockTrackingEnabled;
//
//    private String metaTitle;
//
//    private String metaKeyword;
//
//    private String metaDescription;
//
//    private Long thumbnailMediaId;
//
//    protected Boolean isActive;

//    private Brand brand;

//    private List<ProductCategory> productCategories = new ArrayList<>();

//    private List<ProductAttributeValue> attributeValues = new ArrayList<>();

//    private List<ProductImage> productImages = new ArrayList<>();

//    private Product parent;

//    private List<Product> products = new ArrayList<>();
}
