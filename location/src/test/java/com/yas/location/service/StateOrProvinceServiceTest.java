package com.yas.location.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.location.LocationApplication;
import com.yas.location.model.Country;
import com.yas.location.model.StateOrProvince;
import com.yas.location.repository.CountryRepository;
import com.yas.location.repository.StateOrProvinceRepository;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceAndCountryGetNameVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceListGetVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvincePostVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceVm;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = LocationApplication.class)
class StateOrProvinceServiceTest {

    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private StateOrProvinceRepository stateOrProvinceRepository;
    @Autowired
    private StateOrProvinceService stateOrProvinceService;

    private Country country;
    private StateOrProvince stateOrProvince1;
    private StateOrProvince stateOrProvince2;

    private void generateTestData() {
        country = countryRepository.save(Country.builder()
            .name("country-1")
            .build());
        stateOrProvince1 = stateOrProvinceRepository.save(StateOrProvince.builder()
            .name("state-or-province-1")
            .country(country)
            .build());
        stateOrProvince2 = stateOrProvinceRepository.save(StateOrProvince.builder()
            .name("state-or-province-2")
            .country(country)
            .build());
    }

    @AfterEach
    void tearDown() {
        stateOrProvinceRepository.deleteAll();
        countryRepository.deleteAll();
    }
    
    @Test
    void getStateOrProvince_WithValidId_Success() {
        generateTestData();
        StateOrProvinceVm stateOrProvinceVm = stateOrProvinceService.findById(stateOrProvince1.getId());
        assertNotNull(stateOrProvinceVm);
        assertEquals("state-or-province-1", stateOrProvinceVm.name());
    }
    
    @Test
    void getStateOrProvince_WithInValidId_ThrowsStateOrProvinceNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> stateOrProvinceService.findById(1L));
        assertEquals(String.format("The state or province %s is not found", "1"), exception.getMessage());
    }
    
    @Test
    void createStateOrProvince_ValidData_Success() {
        generateTestData();
        StateOrProvincePostVm stateOrProvincePostVm = StateOrProvincePostVm.builder()
            .countryId(country.getId())
            .name("state-or-province")
            .code("STATE")
            .build();
        StateOrProvince stateOrProvince = stateOrProvinceService.createStateOrProvince(stateOrProvincePostVm);
        assertEquals("STATE", stateOrProvince.getCode());
    }
    
    @Test
    void createStateOrProvince_WithNameExisted_ThrowsNameAlreadyExistException() {
        generateTestData();
        StateOrProvincePostVm stateOrProvincePostVm = StateOrProvincePostVm.builder()
            .countryId(country.getId())
            .name("state-or-province-1")
            .code("STATE")
            .build();
        DuplicatedException exception = assertThrows(DuplicatedException.class, () -> stateOrProvinceService.createStateOrProvince(stateOrProvincePostVm));
        assertEquals(String.format("Request name %s is already existed", "state-or-province-1"), exception.getMessage());
    }
    
    @Test
    void createStateOrProvince_WithCountryNotExist_ThrowsCountryNotFoundException() {
        StateOrProvincePostVm stateOrProvincePostVm = StateOrProvincePostVm.builder()
            .countryId(1L)
            .name("state-or-province-1")
            .code("STATE")
            .build();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> stateOrProvinceService.createStateOrProvince(stateOrProvincePostVm));
        assertEquals(String.format("The country %s is not found", "1"), exception.getMessage());
    }
    
    @Test
    void updateStateOrProvince_WithValidData_Success() {
        generateTestData();
        StateOrProvincePostVm stateOrProvincePostVm = StateOrProvincePostVm.builder()
            .countryId(country.getId())
            .name("state-or-province-update")
            .code("STATE")
            .build();
        stateOrProvinceService.updateStateOrProvince(stateOrProvincePostVm, stateOrProvince1.getId());
        // Get the updated state-or-province to check
        StateOrProvinceVm stateOrProvinceVm = stateOrProvinceService.findById(stateOrProvince1.getId());
        assertNotNull(stateOrProvinceVm);
        assertEquals("state-or-province-update", stateOrProvinceVm.name());
    }

    @Test
    void updateStateOrProvince_WithInValidId_ThrowsStateOrProvinceNotFound() {
        generateTestData();
        StateOrProvincePostVm stateOrProvincePostVm = StateOrProvincePostVm.builder()
            .countryId(country.getId())
            .name("state-or-province-update")
            .code("STATE")
            .build();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> stateOrProvinceService.updateStateOrProvince(stateOrProvincePostVm, 1000L));
        assertEquals(String.format("The state or province %s is not found", "1000"), exception.getMessage());
    }

    @Test
    void updateStateOrProvince_WithNameExisted_ThrowsNameAlreadyExistedException() {
        generateTestData();
        StateOrProvincePostVm stateOrProvincePostVm = StateOrProvincePostVm.builder()
            .countryId(country.getId())
            .name("state-or-province-2")
            .code("STATE")
            .build();
        Long stateOrProvinceId = stateOrProvince1.getId();
        DuplicatedException exception = assertThrows(DuplicatedException.class,
            () -> stateOrProvinceService.updateStateOrProvince(stateOrProvincePostVm, stateOrProvinceId));
        assertEquals(String.format("Request name %s is already existed", "state-or-province-2"), exception.getMessage());
    }

    @Test
    void deleteStateOrProvince_WithExisted_Success() {
        generateTestData();
        stateOrProvinceService.delete(stateOrProvince1.getId());
        // Get the state-or-province which deleted -> got exception
        Long stateOrProvinceId = stateOrProvince1.getId();
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> stateOrProvinceService.findById(stateOrProvinceId));
        assertEquals(String.format("The state or province %s is not found", stateOrProvince1.getId()), exception.getMessage());
    }

    @Test
    void deleteStateOrProvince_WithInvalidId_ThrowsStateOrProvinceNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> stateOrProvinceService.delete(1L));
        assertEquals(String.format("The state or province %s is not found", "1"), exception.getMessage());
    }

    @Test
    void getStateOrProvinceAndCountryName_Success() {
        generateTestData();
        List<StateOrProvinceAndCountryGetNameVm> stateOrProvinceAndCountryGetNameVms =
            stateOrProvinceService.getStateOrProvinceAndCountryNames(List.of(stateOrProvince1.getId(), stateOrProvince2.getId()));
        assertNotNull(stateOrProvinceAndCountryGetNameVms);
        assertEquals("country-1", stateOrProvinceAndCountryGetNameVms.getFirst().countryName());
    }

    @Test
    void getAllStateOrProvinces_Success() {
        generateTestData();
        List<StateOrProvinceVm> stateOrProvinceVms = stateOrProvinceService.findAll();
        assertNotNull(stateOrProvinceVms);
        assertEquals(2, stateOrProvinceVms.size());
    }

    @Test
    void getAllStateOrProvinceByCountryId_Success() {
        generateTestData();
        List<StateOrProvinceVm> stateOrProvinceVms = stateOrProvinceService.getAllByCountryId(country.getId());
        assertNotNull(stateOrProvinceVms);
        assertEquals(2, stateOrProvinceVms.size());
    }

    @Test
    void getStateOrProvincePagination_Success() {
        generateTestData();
        int pageNo = 0;
        int pageSize = 2;
        StateOrProvinceListGetVm stateOrProvinceListGetVm = stateOrProvinceService.getPageableStateOrProvinces(pageNo, pageSize, country.getId());
        assertNotNull(stateOrProvinceListGetVm);
        assertEquals(stateOrProvinceListGetVm.pageNo(), pageNo);
        assertEquals(stateOrProvinceListGetVm.pageSize(), pageSize);
        assertEquals(2, stateOrProvinceListGetVm.stateOrProvinceContent().size());
    }
}
