package com.yas.order.viewmodel.product;

import lombok.Builder;
import lombok.Data;

@Builder(toBuilder = true)
@Data
public class ProductCheckoutListVm {
    Long id;
    String name;
    Double price;
    Long taxClassId;
}