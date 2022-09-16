package com.yas.product.viewmodel;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public record ProductPutVm(
        @NotEmpty String name,
        @NotEmpty String slug,
        Long brandId,
        List<Long> categoryIds,
        @NotEmpty String shortDescription,
        String description,
        @NotEmpty String specification,
        @NotEmpty String sku,
        @NotEmpty String gtin,
        @NotEmpty String metaKeyword,
        String metaDescription,
        MultipartFile thumbnail
//        MultipartFile thumbnail
    )
{
}