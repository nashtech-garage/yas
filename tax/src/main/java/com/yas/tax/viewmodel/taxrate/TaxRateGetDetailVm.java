package com.yas.tax.viewmodel.taxrate;

import com.yas.tax.model.TaxRate;

public record TaxRateGetDetailVm(Long id, Double rate, String zipCode, String taxClassName, String stateOrProvinceName, String countryName) {

}
