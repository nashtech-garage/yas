package com.yas.location.viewmodel.country;

import com.yas.location.model.Country;

public record CountryVm(Long id, String code2, String name, String code3, Boolean isBillingEnabled,
                        Boolean isShippingEnabled,
                        Boolean isCityEnabled, Boolean isZipCodeEnabled,
                        Boolean isDistrictEnabled) {

    public static CountryVm fromModel(Country country) {
        return new CountryVm(country.getId(), country.getCode2(), country.getName(), country.getCode3(),
            country.getIsBillingEnabled(), country.getIsShippingEnabled(),
            country.getIsCityEnabled(), country.getIsZipCodeEnabled(), country.getIsDistrictEnabled());
    }
}
