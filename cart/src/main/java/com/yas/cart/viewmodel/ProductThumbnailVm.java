package com.yas.cart.viewmodel;

import lombok.Builder;

@Builder
public record ProductThumbnailVm(long id, String name, String slug, String thumbnailUrl) {

}
