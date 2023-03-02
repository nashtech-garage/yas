package com.yas.product.viewmodel.productattribute;

import jakarta.validation.constraints.NotEmpty;

public record ProductAttributePostVm(@NotEmpty String name , Long productAttributeGroupId) {
}
