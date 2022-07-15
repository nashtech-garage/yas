package com.yas.product.viewmodel;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public record ProductPostVm(
        @NotEmpty String name,
        @NotEmpty String slug,
        String shortDescription,
        String description,
        String specification,
        String sku,
        String gtin,
        String metaKeyword,
        String metaDescription,
        @NotNull MultipartFile thumbnail) {
}
