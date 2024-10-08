package com.yas.order.viewmodel.product;

import lombok.Builder;

@Builder
public record ProductThumbnailGetVm(long id,
                                    String name,
                                    String slug,
                                    String thumbnailUrl,
                                    Double price) {
}