package com.yas.product.viewmodel;

import javax.validation.constraints.NotEmpty;

public record ProductAttributeValuePostVm( Long ProductId , Long productAttributeId, @NotEmpty String value) {
}
