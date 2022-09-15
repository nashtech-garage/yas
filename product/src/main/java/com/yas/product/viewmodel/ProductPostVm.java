package com.yas.product.viewmodel;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
                String metaKeyword,
                String metaDescription,
                @NotNull MultipartFile thumbnail,
                MultipartFile[] productImages) {
}
