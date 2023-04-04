package com.yas.location.viewmodel.stateorprovince;

import com.yas.location.model.StateOrProvince;

public record StateOrProvinceAndCountryGetNameVm(Long stateOrProvinceId, String stateOrProvinceName, String countryName) {
    public static StateOrProvinceAndCountryGetNameVm fromModel(StateOrProvince stateOrProvince) {
        return new StateOrProvinceAndCountryGetNameVm(stateOrProvince.getId(), stateOrProvince.getName(), stateOrProvince.getCountry().getName());
    }
}
