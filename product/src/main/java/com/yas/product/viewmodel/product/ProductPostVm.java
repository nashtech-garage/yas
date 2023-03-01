package com.yas.product.viewmodel.product;

import com.yas.product.validation.ValidateProductPrice;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public record ProductPostVm(
                @NotEmpty String name,
                @NotEmpty String slug,
                Long brandId,
                List<Long> categoryIds,
                String shortDescription,
                String description,
                String specification,
                String sku,
                String gtin,
                @ValidateProductPrice Double price,
                Boolean isAllowedToOrder,
                Boolean isPublished,
                Boolean isFeatured,

                Boolean isVisibleIndividually ,
                String metaTitle,
                String metaKeyword,
                String metaDescription,
                Long parentId) {
}
