package com.yas.product.viewmodel;

import com.yas.product.model.Product;
import com.yas.product.model.ProductRating;

import java.util.Date;

public record ProductRatingVm(Long id, String content, int star, String customerId, Date createdDate) {
    public static ProductRatingVm fromModel(ProductRating productRating) {
        return new ProductRatingVm(productRating.getId(),
                productRating.getContent(),
                productRating.getRatingStar(),
                productRating.getCustomerId(),
                productRating.getCreatedDate()
                );
    }
}