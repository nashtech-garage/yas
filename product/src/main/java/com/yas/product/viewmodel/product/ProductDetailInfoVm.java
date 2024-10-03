package com.yas.product.viewmodel.product;

import com.yas.product.model.Category;
import com.yas.product.viewmodel.productattribute.ProductAttributeValueGetVm;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProductDetailInfoVm {
    private long id;
    private String name;
    private String shortDescription;
    private String description;
    private String specification;
    private String sku;
    private String gtin;
    private String slug;
    private Boolean isAllowedToOrder;
    private Boolean isPublished;
    private Boolean isFeatured;
    private Boolean isVisible;
    private Boolean stockTrackingEnabled;
    private Double price;
    private Long brandId;
    private List<Category> categories;
    private String metaTitle;
    private String metaKeyword;
    private String metaDescription;
    private Long taxClassId;
    private String brandName;
    private List<ProductAttributeValueGetVm> attributeValues;
    private List<ProductVariationGetVm> variations;

    public ProductDetailInfoVm(long id, String name, String shortDescription, String description,
                               String specification, String sku, String gtin, String slug,
                               Boolean isAllowedToOrder, Boolean isPublished, Boolean isFeatured,
                               Boolean isVisible, Boolean stockTrackingEnabled, Double price,
                               Long brandId, List<Category> categories, String metaTitle,
                               String metaKeyword, String metaDescription, Long taxClassId,
                               String brandName, List<ProductAttributeValueGetVm> attributeValues,
                               List<ProductVariationGetVm> variations) {
        this.id = id;
        this.name = name;
        this.shortDescription = shortDescription;
        this.description = description;
        this.specification = specification;
        this.sku = sku;
        this.gtin = gtin;
        this.slug = slug;
        this.isAllowedToOrder = isAllowedToOrder;
        this.isPublished = isPublished;
        this.isFeatured = isFeatured;
        this.isVisible = isVisible;
        this.stockTrackingEnabled = stockTrackingEnabled;
        this.price = price;
        this.brandId = brandId;
        this.categories = (categories != null) ? categories : new ArrayList<>();  // Handle null
        this.metaTitle = metaTitle;
        this.metaKeyword = metaKeyword;
        this.metaDescription = metaDescription;
        this.taxClassId = taxClassId;
        this.brandName = brandName;
        this.attributeValues = (attributeValues != null) ? attributeValues : new ArrayList<>();  // Handle null
        this.variations = (variations != null) ? variations : new ArrayList<>();  // Handle null
    }
}
