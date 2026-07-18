package com.yas.location.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private DistrictRepository districtRepository;
    @Mock
    private StateOrProvinceRepository stateOrProvinceRepository;

    @InjectMocks
    private AddressService addressService;

    private Address address;
    private Country country;
    private District district;
    private StateOrProvince stateOrProvince;
    private AddressPostVm addressPostVm;

    @BeforeEach
    void setUp() {
        country = Country.builder().id(1L).name("Country 1").build();
        stateOrProvince = StateOrProvince.builder().id(2L).name("State 1").build();
        district = District.builder().id(3L).name("District 1").build();

        address = Address.builder()
            .id(4L)
            .contactName("Contact Name")
            .addressLine1("Line 1")
            .city("City 1")
            .zipCode("12345")
            .phone("0123456789")
            .country(country)
            .stateOrProvince(stateOrProvince)
            .district(district)
            .build();

        addressPostVm = AddressPostVm.builder()
            .contactName("Contact Name Update")
            .addressLine1("Line 1 Update")
            .city("City 1 Update")
            .zipCode("54321")
            .phone("9876543210")
            .countryId(1L)
            .stateOrProvinceId(2L)
            .districtId(3L)
            .build();
    }

    @Test
    void createAddress_WhenValid_ShouldReturnVm() {
        when(stateOrProvinceRepository.findById(2L)).thenReturn(Optional.of(stateOrProvince));
        when(countryRepository.findById(1L)).thenReturn(Optional.of(country));
        when(districtRepository.findById(3L)).thenReturn(Optional.of(district));
        when(addressRepository.save(any(Address.class))).thenAnswer(i -> {
            Address a = i.getArgument(0);
            a.setId(4L);
            return a;
        });

        AddressGetVm result = addressService.createAddress(addressPostVm);

        assertThat(result.id()).isEqualTo(4L);
        assertThat(result.contactName()).isEqualTo("Contact Name Update");
    }

    @Test
    void createAddress_WhenCountryNotFound_ShouldThrowException() {
        when(stateOrProvinceRepository.findById(2L)).thenReturn(Optional.of(stateOrProvince));
        when(countryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> addressService.createAddress(addressPostVm));
    }

    @Test
    void updateAddress_WhenValid_ShouldUpdate() {
        when(addressRepository.findById(4L)).thenReturn(Optional.of(address));
        when(stateOrProvinceRepository.findById(2L)).thenReturn(Optional.of(stateOrProvince));
        when(countryRepository.findById(1L)).thenReturn(Optional.of(country));
        when(districtRepository.findById(3L)).thenReturn(Optional.of(district));

        addressService.updateAddress(4L, addressPostVm);

        verify(addressRepository).save(address);
        assertThat(address.getContactName()).isEqualTo("Contact Name Update");
    }

    @Test
    void updateAddress_WhenAddressNotFound_ShouldThrowException() {
        when(addressRepository.findById(4L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> addressService.updateAddress(4L, addressPostVm));
    }

    @Test
    void getAddressList_ShouldReturnList() {
        when(addressRepository.findAllByIdIn(List.of(4L))).thenReturn(List.of(address));

        List<AddressDetailVm> result = addressService.getAddressList(List.of(4L));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(4L);
    }

    @Test
    void getAddress_WhenValid_ShouldReturnVm() {
        when(addressRepository.findById(4L)).thenReturn(Optional.of(address));

        AddressDetailVm result = addressService.getAddress(4L);

        assertThat(result.id()).isEqualTo(4L);
    }

    @Test
    void getAddress_WhenNotFound_ShouldThrowException() {
        when(addressRepository.findById(4L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> addressService.getAddress(4L));
    }

    @Test
    void deleteAddress_WhenValid_ShouldDelete() {
        when(addressRepository.findById(4L)).thenReturn(Optional.of(address));

        addressService.deleteAddress(4L);

        verify(addressRepository).delete(address);
    }

    @Test
    void deleteAddress_WhenNotFound_ShouldThrowException() {
        when(addressRepository.findById(4L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> addressService.deleteAddress(4L));
    }
}
