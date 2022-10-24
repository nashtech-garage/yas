package com.yas.product.viewmodel;

import javax.validation.constraints.NotEmpty;
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
                Double price,
                Boolean isAllowedToOrder,
                Boolean isPublished,
                Boolean isFeatured,
                Boolean isVisibleIndividually,
                String metaTitle,
                String metaKeyword,
                String metaDescription,
                Long parentId) {
}
