package com.yas.inventory.viewmodel.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Matches the fields product-service actually returns for /backoffice/products/{id}
// and /backoffice/products/for-warehouse. Kept separate from ProductInfoVm (which also
// carries existInWh, a value inventory computes locally and product-service never sends)
// so response deserialization never fails on a field the source never had.
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductWireVm(Long id, String name, String sku) {
}
