package com.yas.product.viewmodel.product;

import com.yas.product.model.Product;
import lombok.Builder;


public record ProductThumbnailGetVm(long id, String name, String slug, String thumbnailUrl, Double price, Double averageStar) {

}
