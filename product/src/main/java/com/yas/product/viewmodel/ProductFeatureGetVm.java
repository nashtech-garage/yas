package com.yas.product.viewmodel;

import java.util.List;

public record ProductFeatureGetVm(List<ProductThumbnailGetVm> productList, int totalPage) {
}
