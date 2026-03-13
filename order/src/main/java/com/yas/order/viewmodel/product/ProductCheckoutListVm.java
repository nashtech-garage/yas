package com.yas.order.viewmodel.product;

import com.yas.order.viewmodel.enumeration.DimensionUnit;
import lombok.Builder;
import lombok.Data;

@Builder(toBuilder = true)
@Data
public class ProductCheckoutListVm {
    Long id;
    String name;
    Double price;
    Long taxClassId;
    Double weight;
    DimensionUnit dimensionUnit;
    Double length;
    Double width;
    Double height;
}