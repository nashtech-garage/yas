package com.yas.product.viewmodel.productoption;

import jakarta.validation.constraints.NotBlank;

public record ProductOptionPostVm(@NotBlank String name) {
}
