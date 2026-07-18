package com.yas.order.viewmodel.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// product's response carries more fields (description, sku, thumbnailUrl, ...)
// than order needs; ignoreUnknown lets this record deserialize that payload
// instead of only the fields listed below.
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductCheckoutListVm(
    Long id,
    String name,
    Double price,
    Long taxClassId
) {}
