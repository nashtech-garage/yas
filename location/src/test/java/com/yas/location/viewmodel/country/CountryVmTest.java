package com.yas.location.viewmodel.country;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.yas.location.model.Country;
import org.junit.jupiter.api.Test;

class CountryVmTest {

    @Test
    void testCountryVmFromModel_Success() {
        Country country = Country.builder()
            .id(1L)
            .code2("US")
            .code3("USA")
            .name("United States")
            .isBillingEnabled(true)
            .isShippingEnabled(true)
            .isCityEnabled(true)
            .isZipCodeEnabled(true)
            .isDistrictEnabled(false)
            .build();

        CountryVm countryVm = CountryVm.fromModel(country);

        assertNotNull(countryVm);
        assertEquals(1L, countryVm.id());
        assertEquals("US", countryVm.code2());
        assertEquals("USA", countryVm.code3());
        assertEquals("United States", countryVm.name());
        assertTrue(countryVm.isBillingEnabled());
        assertTrue(countryVm.isShippingEnabled());
        assertTrue(countryVm.isCityEnabled());
        assertTrue(countryVm.isZipCodeEnabled());
        assertFalse(countryVm.isDistrictEnabled());
    }

    @Test
    void testCountryVmRecord_Success() {
        CountryVm countryVm = new CountryVm(1L, "CA", "Canada", "CAN", true, true, false, true, false);

        assertNotNull(countryVm);
        assertEquals(1L, countryVm.id());
        assertEquals("CA", countryVm.code2());
        assertEquals("Canada", countryVm.name());
    }

    @Test
    void testCountryVmFromModelWithNullValues_Success() {
        Country country = Country.builder()
            .id(2L)
            .code2("UK")
            .name("United Kingdom")
            .build();

        CountryVm countryVm = CountryVm.fromModel(country);

        assertNotNull(countryVm);
        assertEquals(2L, countryVm.id());
        assertEquals("UK", countryVm.code2());
        assertEquals("United Kingdom", countryVm.name());
    }

    @Test
    void testCountryVmAllFields_Success() {
        CountryVm countryVm = new CountryVm(3L, "FR", "France", "FRA", true, false, true, false, true);

        assertEquals(3L, countryVm.id());
        assertEquals("FR", countryVm.code2());
        assertEquals("France", countryVm.name());
        assertEquals("FRA", countryVm.code3());
        assertTrue(countryVm.isBillingEnabled());
        assertFalse(countryVm.isShippingEnabled());
    }

    @Test
    void testCountryPostVmBuilder_Success() {
        CountryPostVm postVm = CountryPostVm.builder()
            .id("id1")
            .code2("JP")
            .name("Japan")
            .code3("JPN")
            .isBillingEnabled(true)
            .isShippingEnabled(true)
            .isCityEnabled(false)
            .isZipCodeEnabled(true)
            .isDistrictEnabled(false)
            .build();

        assertNotNull(postVm);
        assertEquals("id1", postVm.id());
        assertEquals("JP", postVm.code2());
        assertEquals("Japan", postVm.name());
    }
}
