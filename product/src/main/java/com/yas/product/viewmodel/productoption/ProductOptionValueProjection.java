package com.yas.product.viewmodel.productoption;

public interface ProductOptionValueProjection {

    String getValue();

    ProductOptionProjection getProductOption();

    interface ProductOptionProjection {

        Long getId();
    }
}
