package com.yas.product.viewmodel.product;

import com.yas.product.model.Product;
import java.time.ZonedDateTime;
import java.util.Objects;

public record ProductCheckoutListVm(Long id,
                                    String name,
                                    String description,
                                    String shortDescription,
                                    String sku,
                                    Long parentId,
                                    Long brandId,
                                    Double price,
                                    Long taxClassId,
                                    ZonedDateTime createdOn,
                                    String createdBy,
                                    ZonedDateTime lastModifiedOn,
                                    String lastModifiedBy) {
    public static ProductCheckoutListVm fromModel(Product product) {
        return new ProductCheckoutListVm(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getShortDescription(),
            product.getSku(),
            Objects.isNull(product.getParent()) ? null : product.getParent().getId(),
            product.getBrand().getId(),
            product.getPrice(),
            product.getTaxClassId(),
            product.getCreatedOn(),
            product.getCreatedBy(),
            product.getLastModifiedOn(),
            product.getLastModifiedBy()
        );
    }
}
