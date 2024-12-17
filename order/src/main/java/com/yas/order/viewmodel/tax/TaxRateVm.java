package com.yas.order.viewmodel.tax;

public record TaxRateVm(Long id, Double rate, String zipCode, Long taxClassId, Long stateOrProvinceId, Long countryId) {

}