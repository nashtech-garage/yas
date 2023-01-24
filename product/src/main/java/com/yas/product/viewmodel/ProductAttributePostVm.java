package com.yas.product.viewmodel;

import jakarta.validation.constraints.NotEmpty;

public record ProductAttributePostVm(@NotEmpty String name , Long productAttributeGroupId) {
}
