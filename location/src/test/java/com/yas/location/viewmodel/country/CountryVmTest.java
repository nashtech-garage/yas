package com.yas.location.viewmodel.country;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.yas.location.model.Country;
import org.junit.jupiter.api.Test;

class CountryVmTest {

    @Test
    void fromModel_mapsFields() {
        Country country = Country.builder()
            .id(7L)
            .code2("US")
            .name("United States")
            .code3("USA")
            .isBillingEnabled(true)
            .isShippingEnabled(false)
            .isCityEnabled(true)
            .isZipCodeEnabled(false)
            .isDistrictEnabled(true)
            .build();

        CountryVm vm = CountryVm.fromModel(country);

        assertEquals(7L, vm.id());
        assertEquals("US", vm.code2());
        assertEquals("United States", vm.name());
        assertEquals("USA", vm.code3());
        assertEquals(true, vm.isBillingEnabled());
        assertEquals(false, vm.isShippingEnabled());
        assertEquals(true, vm.isCityEnabled());
        assertEquals(false, vm.isZipCodeEnabled());
        assertEquals(true, vm.isDistrictEnabled());
    }
}
