package com.yas.location.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class CountryTest {

    @Test
    void testCountryBuilder_Success() {
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

        assertNotNull(country);
        assertEquals(1L, country.getId());
        assertEquals("US", country.getCode2());
        assertEquals("USA", country.getCode3());
        assertEquals("United States", country.getName());
        assertTrue(country.getIsBillingEnabled());
        assertTrue(country.getIsShippingEnabled());
        assertTrue(country.getIsCityEnabled());
        assertTrue(country.getIsZipCodeEnabled());
        assertFalse(country.getIsDistrictEnabled());
    }

    @Test
    void testCountryNoArgsConstructor_Success() {
        Country country = new Country();
        assertNotNull(country);
        assertNotNull(country.getStateOrProvinces());
        assertEquals(0, country.getStateOrProvinces().size());
    }

    @Test
    void testCountrySetters_Success() {
        Country country = new Country();
        country.setId(2L);
        country.setCode2("CA");
        country.setCode3("CAN");
        country.setName("Canada");
        country.setIsBillingEnabled(true);
        country.setIsShippingEnabled(true);
        country.setIsCityEnabled(true);
        country.setIsZipCodeEnabled(true);
        country.setIsDistrictEnabled(false);

        assertEquals(2L, country.getId());
        assertEquals("CA", country.getCode2());
        assertEquals("CAN", country.getCode3());
        assertEquals("Canada", country.getName());
        assertTrue(country.getIsBillingEnabled());
    }

    @Test
    void testCountryStateOrProvinces_Success() {
        Country country = Country.builder()
            .id(1L)
            .name("Test Country")
            .build();

        StateOrProvince state1 = StateOrProvince.builder()
            .id(1L)
            .name("State 1")
            .country(country)
            .build();

        StateOrProvince state2 = StateOrProvince.builder()
            .id(2L)
            .name("State 2")
            .country(country)
            .build();

        List<StateOrProvince> states = new ArrayList<>();
        states.add(state1);
        states.add(state2);

        country.setStateOrProvinces(states);

        assertEquals(2, country.getStateOrProvinces().size());
        assertTrue(country.getStateOrProvinces().contains(state1));
        assertTrue(country.getStateOrProvinces().contains(state2));
    }

    @Test
    void testCountryNullValues_Success() {
        Country country = Country.builder().id(1L).name("Test").build();
        assertNotNull(country);
    }

    @Test
    void testCountryAllArgsConstructor_Success() {
        List<StateOrProvince> states = new ArrayList<>();
        Country country = new Country(1L, "Test Country", "TC", "TCT", true, true, true, true, false, states);

        assertEquals(1L, country.getId());
        assertEquals("Test Country", country.getName());
        assertEquals("TC", country.getCode2());
        assertTrue(country.getIsBillingEnabled());
        assertEquals(states, country.getStateOrProvinces());
    }
}
