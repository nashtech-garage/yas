package com.yas.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductDetailDTO(
        long id,
        String name,
        String shortDescription,
        String description,
        String specification,
        String sku,
        String gtin,
        String slug,
        Boolean isAllowedToOrder,
        Boolean isPublished,
        Boolean isFeatured,
        Boolean isVisible,
        Boolean stockTrackingEnabled,
        Double price,
        Long brandId,
        List<CategoryDTO> categories,
        String metaTitle,
        String metaKeyword,
        String metaDescription,
        Long taxClassId,
        String brandName,
        List<ProductAttributeValueDTO> attributeValues,
        List<ProductVariationDTO> variations,
        ImageDTO thumbnail,
        List<ImageDTO> productImages
) {
}
