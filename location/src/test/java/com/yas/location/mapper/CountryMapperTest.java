package com.yas.location.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.location.LocationApplication;
import com.yas.location.model.Country;
import com.yas.location.viewmodel.country.CountryPostVm;
import com.yas.location.viewmodel.country.CountryVm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = LocationApplication.class)
class CountryMapperTest {

    @Autowired
    private CountryMapper countryMapper;

    @Test
    void testToCountryFromCountryPostViewModel_Success() {
        CountryPostVm postVm = CountryPostVm.builder()
            .id("id1")
            .code2("US")
            .code3("USA")
            .name("United States")
            .isBillingEnabled(true)
            .isShippingEnabled(true)
            .isCityEnabled(true)
            .isZipCodeEnabled(true)
            .isDistrictEnabled(false)
            .build();

        Country country = countryMapper.toCountryFromCountryPostViewModel(postVm);

        assertNotNull(country);
        assertEquals("US", country.getCode2());
        assertEquals("USA", country.getCode3());
        assertEquals("United States", country.getName());
        assertEquals(true, country.getIsBillingEnabled());
    }

    @Test
    void testToCountryViewModelFromCountry_Success() {
        Country country = Country.builder()
            .id(1L)
            .code2("CA")
            .code3("CAN")
            .name("Canada")
            .isBillingEnabled(true)
            .isShippingEnabled(false)
            .isCityEnabled(true)
            .isZipCodeEnabled(false)
            .isDistrictEnabled(false)
            .build();

        CountryVm countryVm = countryMapper.toCountryViewModelFromCountry(country);

        assertNotNull(countryVm);
        assertEquals(1L, countryVm.id());
        assertEquals("CA", countryVm.code2());
        assertEquals("CAN", countryVm.code3());
        assertEquals("Canada", countryVm.name());
        assertEquals(true, countryVm.isBillingEnabled());
        assertEquals(false, countryVm.isShippingEnabled());
    }

    @Test
    void testToCountryFromCountryPostViewModelWithNullValues_Success() {
        CountryPostVm postVm = CountryPostVm.builder()
            .id("id2")
            .code2("UK")
            .name("United Kingdom")
            .build();

        Country country = countryMapper.toCountryFromCountryPostViewModel(postVm);

        assertNotNull(country);
        assertEquals("UK", country.getCode2());
        assertEquals("United Kingdom", country.getName());
    }

    @Test
    void testToCountryFromCountryPostViewModelUpdate_Success() {
        Country country = Country.builder()
            .id(1L)
            .code2("OLD")
            .name("Old Name")
            .build();

        CountryPostVm postVm = CountryPostVm.builder()
            .id("id3")
            .code2("NEW")
            .name("New Name")
            .isBillingEnabled(true)
            .build();

        countryMapper.toCountryFromCountryPostViewModel(country, postVm);

        assertNotNull(country);
        assertEquals("NEW", country.getCode2());
        assertEquals("New Name", country.getName());
        assertEquals(true, country.getIsBillingEnabled());
    }

    @Test
    void testToCountryFromCountryPostViewModelUpdatePartial_Success() {
        Country country = Country.builder()
            .id(1L)
            .code2("US")
            .code3("USA")
            .name("United States")
            .isBillingEnabled(true)
            .build();

        CountryPostVm postVm = CountryPostVm.builder()
            .id("id4")
            .code2("CA")
            .name("Canada")
            .build();

        countryMapper.toCountryFromCountryPostViewModel(country, postVm);

        assertEquals("CA", country.getCode2());
        assertEquals("Canada", country.getName());
        assertEquals(true, country.getIsBillingEnabled());
    }

    @Test
    void testMapperPreservesId_Success() {
        CountryPostVm postVm = CountryPostVm.builder()
            .id("anyId")
            .code2("JP")
            .name("Japan")
            .build();

        Country country = countryMapper.toCountryFromCountryPostViewModel(postVm);

        // ID should be null because it's ignored in mapping
        assertNotNull(country);
        assertEquals("JP", country.getCode2());
    }

    @Test
    void testMapperWithAllBooleanFlags_Success() {
        CountryPostVm postVm = CountryPostVm.builder()
            .id("id5")
            .code2("FR")
            .name("France")
            .isBillingEnabled(true)
            .isShippingEnabled(true)
            .isCityEnabled(false)
            .isZipCodeEnabled(true)
            .isDistrictEnabled(true)
            .build();

        Country country = countryMapper.toCountryFromCountryPostViewModel(postVm);

        assertEquals(true, country.getIsBillingEnabled());
        assertEquals(true, country.getIsShippingEnabled());
        assertEquals(false, country.getIsCityEnabled());
        assertEquals(true, country.getIsZipCodeEnabled());
        assertEquals(true, country.getIsDistrictEnabled());
    }
}
