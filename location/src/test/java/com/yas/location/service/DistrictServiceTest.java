package com.yas.location.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.location.LocationApplication;
import com.yas.location.model.Country;
import com.yas.location.model.District;
import com.yas.location.model.StateOrProvince;
import com.yas.location.repository.CountryRepository;
import com.yas.location.repository.DistrictRepository;
import com.yas.location.repository.StateOrProvinceRepository;
import com.yas.location.viewmodel.district.DistrictGetVm;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = LocationApplication.class)
class DistrictServiceTest {

    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private StateOrProvinceRepository stateOrProvinceRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private DistrictService districtService;

    private District district1;
    private Country country;
    private StateOrProvince stateOrProvince;

    private void generateTestData() {
        country = countryRepository.save(Country.builder()
            .name("country-1")
            .build());
        stateOrProvince = stateOrProvinceRepository.save(StateOrProvince.builder()
            .name("state-or-province")
            .country(country)
            .build());
        district1 = districtRepository.save(District.builder()
            .name("district-1")
            .stateProvince(stateOrProvince)
            .build());
    }

    @AfterEach
    void tearDown() {
        districtRepository.deleteAll();
        stateOrProvinceRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void getDistrict_WithValidId_Success() {
        generateTestData();
        List<DistrictGetVm> districtGetVm = districtService.getList(district1.getId());
        assertNotNull(districtGetVm);
    }

    @Test
    void testGetList_WithValidStateId_Success() {
        generateTestData();
        List<DistrictGetVm> result = districtService.getList(stateOrProvince.getId());
        assertNotNull(result);
    }

    @Test
    void testGetList_WithMultipleDistricts_Success() {
        country = countryRepository.save(Country.builder().code2("XX").name("Country").build());
        stateOrProvince = stateOrProvinceRepository.save(StateOrProvince.builder()
            .name("State")
            .code("ST")
            .country(country)
            .build());
        
        for (int i = 1; i <= 3; i++) {
            districtRepository.save(District.builder()
                .name("District " + i)
                .stateProvince(stateOrProvince)
                .build());
        }
        
        List<DistrictGetVm> result = districtService.getList(stateOrProvince.getId());
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testGetList_WithEmptyResult_Success() {
        generateTestData();
        StateOrProvince emptyState = stateOrProvinceRepository.save(StateOrProvince.builder()
            .name("Empty State")
            .code("ES")
            .country(country)
            .build());

        List<DistrictGetVm> result = districtService.getList(emptyState.getId());
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testGetList_Ordering_Success() {
        country = countryRepository.save(Country.builder().code2("YY").name("Country").build());
        stateOrProvince = stateOrProvinceRepository.save(StateOrProvince.builder()
            .name("State")
            .code("ST")
            .country(country)
            .build());
        
        districtRepository.save(District.builder().name("Zebra").stateProvince(stateOrProvince).build());
        districtRepository.save(District.builder().name("Apple").stateProvince(stateOrProvince).build());
        districtRepository.save(District.builder().name("Mango").stateProvince(stateOrProvince).build());
        
        List<DistrictGetVm> result = districtService.getList(stateOrProvince.getId());
        assertEquals(3, result.size());
        assertEquals("Apple", result.get(0).name());
    }
}
