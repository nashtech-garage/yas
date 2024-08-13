package com.yas.location.mapper;

import com.yas.location.model.Country;
import com.yas.location.viewmodel.country.CountryPostVm;
import com.yas.location.viewmodel.country.CountryVm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CountryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "code2", source = "code2")
    @Mapping(target = "code3", source = "code3")
    @Mapping(target = "isBillingEnabled", source = "isBillingEnabled")
    @Mapping(target = "isShippingEnabled", source = "isShippingEnabled")
    @Mapping(target = "isCityEnabled", source = "isCityEnabled")
    @Mapping(target = "isZipCodeEnabled", source = "isZipCodeEnabled")
    @Mapping(target = "isDistrictEnabled", source = "isDistrictEnabled")
    Country toCountryFromCountryPostViewModel(CountryPostVm countryPostVm);

    @Mapping(target = "id", ignore = true)
    void toCountryFromCountryPostViewModel(@MappingTarget Country country, CountryPostVm dto);

    CountryVm toCountryViewModelFromCountry(Country country);
}
