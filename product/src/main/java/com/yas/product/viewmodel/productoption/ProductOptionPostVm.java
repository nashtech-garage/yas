package com.yas.product.viewmodel.productoption;

import jakarta.validation.constraints.NotEmpty;

public record ProductOptionPostVm(@NotEmpty String name) {
}
