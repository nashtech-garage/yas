package com.yas.product.viewmodel;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public record ProductPutVm(
        @NotEmpty String name,
        @NotEmpty String slug,
        @DecimalMin(value = "0.0") Double price,
        Boolean isAllowedToOrder,
        Boolean isPublished,
        Boolean isFeatured,
        @NotNull Long brandId,
        @NotNull List<Long> categoryIds,
        @NotEmpty String shortDescription,
        String description,
        @NotEmpty String specification,
        @NotEmpty String sku,
        @NotEmpty String gtin,
        @NotEmpty String metaKeyword,
        String metaDescription,
        @NotNull MultipartFile thumbnail,
        @NotNull List<MultipartFile> productImages
) {
}