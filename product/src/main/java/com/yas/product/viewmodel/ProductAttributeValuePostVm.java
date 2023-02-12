package com.yas.product.viewmodel;

import jakarta.validation.constraints.NotEmpty;

public record ProductAttributeValuePostVm( Long ProductId , Long productAttributeId, String value) {
}
