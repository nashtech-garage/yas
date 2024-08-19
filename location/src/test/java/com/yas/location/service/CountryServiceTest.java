package com.yas.location.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.yas.location.LocationApplication;
import com.yas.location.exception.DuplicatedException;
import com.yas.location.exception.NotFoundException;
import com.yas.location.model.Country;
import com.yas.location.repository.CountryRepository;

import com.yas.location.viewmodel.country.CountryListGetVm;
import com.yas.location.viewmodel.country.CountryPostVm;
import com.yas.location.viewmodel.country.CountryVm;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = LocationApplication.class)
public class CountryServiceTest {

    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CountryService countryService;
    private Country country1;
    private Country country2;

    @BeforeEach
    void setUp() {

    }

    private void generateTestData() {
        country1 = countryRepository.save(Country.builder()
            .name("country-1")
            .build());
        country2 = countryRepository.save(Country.builder()
            .name("country-1")
            .build());
    }

    @AfterEach
    void tearDown() {
        countryRepository.deleteAll();
    }

    @Test
    void getCountry_ExistInDatabase_Success() {
        generateTestData();
        CountryVm countryVm = countryService.findById(country1.getId());
        assertNotNull(countryVm);
        assertEquals("country-1", countryVm.name());
    }

    @Test
    void getCountry_NotInDatabase_ThrowsCountryNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> countryService.findById(1L));
        assertEquals(String.format("The country %s is not found", "1"), exception.getMessage());
    }

    @Test
    void getAllCountries_Success() {
        generateTestData();
        List<CountryVm> countryVms = countryService.findAllCountries();
        assertEquals(2, countryVms.size());
    }

    @Test
    void createCountry_ValidData_Success() {
        CountryPostVm countryPostVm = CountryPostVm.builder()
            .name("country")
            .build();
        Country country = countryService.create(countryPostVm);
        assertNotNull(country);
        assertEquals(country.getName(), "country");
    }

    @Test
    void createCountry_WithNameExisted_ThrowsNamAlreadyExistedException() {
        generateTestData();
        CountryPostVm countryPostVm = CountryPostVm.builder()
            .name("country-1")
            .build();
        DuplicatedException exception = assertThrows(DuplicatedException.class, () -> countryService.create(countryPostVm));
        assertEquals(String.format("Request name %s is already existed", "country-1"), exception.getMessage());
    }

    @Test
    void updateCountry_ValidData_Success() {
        generateTestData();
        CountryPostVm countryPostVm = CountryPostVm.builder()
            .name("country-1-update")
            .build();
        countryService.update(countryPostVm, country1.getId());
        // Get the country after update
        CountryVm countryVm = countryService.findById(country1.getId());
        assertNotNull(countryVm);
        assertEquals("country-1-update", countryVm.name());
    }

    @Test
    void updateCountry_WithIdNotValid_ThrowsCountryNotFoundException() {
        CountryPostVm countryPostVm = CountryPostVm.builder()
            .name("country-1-update")
            .build();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> countryService.update(countryPostVm,1L));
        assertEquals(String.format("The country %s is not found", "1"), exception.getMessage());
    }

    @Test
    void updateCountry_WithNameExisted_ThrowsNameAlreadyExistedException() {
        generateTestData();
        CountryPostVm countryPostVm = CountryPostVm.builder()
            .name("country-1")
            .build();
        DuplicatedException exception = assertThrows(DuplicatedException.class, () -> countryService.update(countryPostVm, country1.getId()));
        assertEquals(String.format("Request name %s is already existed", "country-1"), exception.getMessage());
    }

    @Test
    void deleteCountry_WithValidId_Success() {
        generateTestData();
        countryService.delete(country1.getId());
        // Get the country with id after delete -> null
        assertThrows(NotFoundException.class, () -> countryService.findById(country1.getId()));
    }

    @Test
    void deleteCountry_WithInValidId_ThrowsCountryNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> countryService.delete(1L));
        assertEquals(String.format("The country %s is not found", "1"), exception.getMessage());
    }

    @Test
    void getCountries_Pagination_Success() {
        generateTestData();
        int pageNo = 1;
        int pageSize = 2;
        CountryListGetVm countryListGetVm = countryService.getPageableCountries(pageNo, pageSize);
        assertNotNull(countryListGetVm);
        assertEquals(countryListGetVm.pageNo(), pageNo);
        assertEquals(countryListGetVm.pageSize(), pageSize);
        assertTrue(countryListGetVm.isLast());
        assertEquals(countryListGetVm.totalElements(), 2);
        assertEquals(countryListGetVm.totalPages(), 1);
    }
}
