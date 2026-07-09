package com.yas.location.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.location.model.Country;
import com.yas.location.viewmodel.country.CountryPostVm;
import com.yas.location.viewmodel.country.CountryVm;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class CountryMapperTest {
    private final CountryMapper countryMapper = Mappers.getMapper(CountryMapper.class);

    @Test
    void toCountryFromCountryPostViewModel_whenCalled_mapCorrect() {
        CountryPostVm postVm = CountryPostVm.builder()
                .name("USA")
                .code2("US")
                .code3("USA")
                .isBillingEnabled(true)
                .isShippingEnabled(true)
                .isCityEnabled(true)
                .isZipCodeEnabled(true)
                .isDistrictEnabled(true)
                .build();

        Country country = countryMapper.toCountryFromCountryPostViewModel(postVm);

        assertThat(country.getName()).isEqualTo("USA");
        assertThat(country.getCode2()).isEqualTo("US");
        assertThat(country.getIsBillingEnabled()).isTrue();
    }

    @Test
    void toCountryViewModelFromCountry_whenCalled_mapCorrect() {
        Country country = Country.builder()
                .id(1L)
                .name("USA")
                .code2("US")
                .build();

        CountryVm vm = countryMapper.toCountryViewModelFromCountry(country);

        assertThat(vm.id()).isEqualTo(1L);
        assertThat(vm.name()).isEqualTo("USA");
    }

    @Test
    void toCountryFromCountryPostViewModel_whenMappingTarget_mapCorrect() {
        Country country = new Country();
        CountryPostVm postVm = CountryPostVm.builder().name("Updated").build();

        countryMapper.toCountryFromCountryPostViewModel(country, postVm);

        assertThat(country.getName()).isEqualTo("Updated");
    }
}
