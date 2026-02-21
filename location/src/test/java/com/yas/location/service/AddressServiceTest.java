package com.yas.location.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.location.LocationApplication;
import com.yas.location.model.Address;
import com.yas.location.model.Country;
import com.yas.location.model.District;
import com.yas.location.model.StateOrProvince;
import com.yas.location.repository.AddressRepository;
import com.yas.location.repository.CountryRepository;
import com.yas.location.repository.DistrictRepository;
import com.yas.location.repository.StateOrProvinceRepository;
import com.yas.location.viewmodel.address.AddressDetailVm;
import com.yas.location.viewmodel.address.AddressGetVm;
import com.yas.location.viewmodel.address.AddressPostVm;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = LocationApplication.class)
class AddressServiceTest {

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private StateOrProvinceRepository stateOrProvinceRepository;
    @Autowired
    private AddressService addressService;

    private Address address1;
    private Address address2;
    private Country country;
    private District district;
    private StateOrProvince stateOrProvince;

    private void generateTestData() {
        country = countryRepository.save(Country.builder()
            .name("country-1")
            .build());
        stateOrProvince = stateOrProvinceRepository.save(StateOrProvince.builder()
            .name("state-or-province")
            .country(country)
            .build());
        district = districtRepository.save(District.builder()
            .name("district-1")
            .stateProvince(stateOrProvince)
            .build());
        address1 = addressRepository.save(Address.builder()
            .contactName("address-1")
            .city("city-1")
            .country(country)
            .district(district)
            .stateOrProvince(stateOrProvince)
            .build());
        address2 = addressRepository.save(Address.builder()
            .contactName("address-1")
            .city("city-1")
            .country(country)
            .district(district)
            .stateOrProvince(stateOrProvince)
            .build());
    }

    @AfterEach
    void tearDown() {
        addressRepository.deleteAll();
        districtRepository.deleteAll();
        stateOrProvinceRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void getAddress_ExistInDatabase_Success() {
        generateTestData();
        AddressDetailVm addressDetailVm = addressService.getAddress(address1.getId());
        assertNotNull(addressDetailVm);
        assertEquals("address-1", addressDetailVm.contactName());
    }

    @Test
    void getAddress_NotExistInDatabase_ThrowsNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> addressService.getAddress(100000L));
        assertEquals(String.format("The address %s is not found", "100000"), exception.getMessage());
    }

    @Test
    void getAllAddresses_Success() {
        generateTestData();
        List<AddressDetailVm> addressDetailVmList = addressService.getAddressList(List.of(address1.getId(), address2.getId()));
        assertEquals(2, addressDetailVmList.size());
    }

    @Test
    void updateAddress_validData_Success() {
        generateTestData();
        AddressPostVm addressPostVm = AddressPostVm.builder()
            .contactName("update-address")
            .districtId(district.getId())
            .countryId(country.getId())
            .stateOrProvinceId(stateOrProvince.getId())
            .build();
        addressService.updateAddress(address1.getId(), addressPostVm);
        AddressDetailVm addressDetailVm = addressService.getAddress(address1.getId());
        assertNotNull(addressDetailVm);
        assertEquals("update-address", addressDetailVm.contactName());
    }

    @Test
    void updateAddress_inValidAddressId_ThrowsAddressNotFoundException() {
        generateTestData();
        AddressPostVm addressPostVm = AddressPostVm.builder()
            .contactName("update-address")
            .districtId(district.getId())
            .countryId(country.getId())
            .stateOrProvinceId(stateOrProvince.getId())
            .build();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> addressService.updateAddress(10000L, addressPostVm));
        assertEquals(String.format("The address %s is not found", "10000"), exception.getMessage());
    }

    @Test
    void createAddress_validDate_Success() {
        generateTestData();
        AddressPostVm addressPostVm = AddressPostVm.builder()
            .contactName("update-address")
            .districtId(district.getId())
            .countryId(country.getId())
            .stateOrProvinceId(stateOrProvince.getId())
            .build();
        AddressGetVm addressGetVm = addressService.createAddress(addressPostVm);
        assertNotNull(addressGetVm);
    }

    @Test
    void createAddress_inValidData_ThrowsCountryNotFoundException() {
        generateTestData();
        AddressPostVm addressPostVm = AddressPostVm.builder()
            .contactName("update-address")
            .districtId(district.getId())
            .countryId(10000L)
            .stateOrProvinceId(stateOrProvince.getId())
            .build();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> addressService.createAddress(addressPostVm));
        assertEquals(String.format("The country %s is not found", "10000"), exception.getMessage());
    }

    @Test
    void deleteAddress_givenAddressIdValid_thenSuccess() {
        generateTestData();
        Long id = addressRepository.findAll().getFirst().getId();
        addressService.deleteAddress(id);
        // make a call to get the address with id which has been deleted -> throw error not found because deleted success.
        NotFoundException exception = assertThrows(NotFoundException.class, () -> addressService.getAddress(100000L));
        assertEquals(String.format("The address %s is not found", "100000"), exception.getMessage());
    }

    @Test
    void deleteAddress_givenAddressIdInValid_ThrowsAddressNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> addressService.deleteAddress(1L));
        assertEquals(String.format("The address %s is not found", "1"), exception.getMessage());
    }
}
