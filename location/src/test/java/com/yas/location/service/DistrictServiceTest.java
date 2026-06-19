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
public class DistrictServiceTest {

    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private StateOrProvinceRepository stateOrProvinceRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private DistrictService districtService;

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
        districtRepository.save(District.builder()
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
        List<DistrictGetVm> districtGetVm = districtService.getList(stateOrProvince.getId());
        assertNotNull(districtGetVm);
        assertEquals(1, districtGetVm.size());
        assertEquals("district-1", districtGetVm.get(0).name());
    }

    @Test
    void getDistrict_WithInvalidId_ReturnsEmptyList() {
        generateTestData();
        List<DistrictGetVm> districtGetVm = districtService.getList(999L);
        assertNotNull(districtGetVm);
        assertEquals(0, districtGetVm.size());
    }
}
