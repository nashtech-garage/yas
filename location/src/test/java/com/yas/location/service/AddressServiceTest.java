package com.yas.location.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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

class AddressServiceTest {
    private AddressRepository addressRepository;
    private StateOrProvinceRepository stateOrProvinceRepository;
    private CountryRepository countryRepository;
    private DistrictRepository districtRepository;
    private AddressService addressService;

    @BeforeEach
    void setUp() {
        addressRepository = mock(AddressRepository.class);
        stateOrProvinceRepository = mock(StateOrProvinceRepository.class);
        countryRepository = mock(CountryRepository.class);
        districtRepository = mock(DistrictRepository.class);
        addressService = new AddressService(addressRepository, stateOrProvinceRepository, countryRepository, districtRepository);
    }

    @Test
    void createAddress_whenCountryNotFound_throwNotFoundException() {
        AddressPostVm dto = AddressPostVm.builder()
                .countryId(1L)
                .build();
        when(countryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.createAddress(dto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void createAddress_whenValid_returnAddressGetVm() {
        AddressPostVm dto = AddressPostVm.builder()
                .contactName("John")
                .countryId(1L)
                .stateOrProvinceId(1L)
                .districtId(1L)
                .build();

        Country country = Country.builder().id(1L).name("Country").build();
        StateOrProvince stateOrProvince = StateOrProvince.builder().id(1L).name("State").build();
        District district = District.builder().id(1L).name("District").build();

        when(countryRepository.findById(1L)).thenReturn(Optional.of(country));
        when(stateOrProvinceRepository.findById(1L)).thenReturn(Optional.of(stateOrProvince));
        when(districtRepository.findById(1L)).thenReturn(Optional.of(district));
        
        Address address = Address.builder()
                .id(1L)
                .contactName("John")
                .country(country)
                .stateOrProvince(stateOrProvince)
                .district(district)
                .build();
        when(addressRepository.save(any())).thenReturn(address);

        AddressGetVm result = addressService.createAddress(dto);

        assertThat(result.id()).isEqualTo(1L);
        verify(addressRepository).save(any());
    }

    @Test
    void createAddress_whenOptionalFieldsNotFound_returnAddressGetVm() {
        AddressPostVm dto = AddressPostVm.builder()
                .countryId(1L)
                .stateOrProvinceId(1L)
                .districtId(1L)
                .build();

        when(countryRepository.findById(1L)).thenReturn(Optional.of(new Country()));
        when(stateOrProvinceRepository.findById(1L)).thenReturn(Optional.empty());
        when(districtRepository.findById(1L)).thenReturn(Optional.empty());
        
        Address address = Address.builder()
                .id(1L)
                .country(new Country())
                .stateOrProvince(new StateOrProvince())
                .district(new District())
                .build();
        when(addressRepository.save(any())).thenReturn(address);

        AddressGetVm result = addressService.createAddress(dto);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void updateAddress_whenAddressNotFound_throwNotFoundException() {
        AddressPostVm dto = AddressPostVm.builder().build();
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.updateAddress(1L, dto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateAddress_whenValid_saveAddress() {
        AddressPostVm dto = AddressPostVm.builder()
                .contactName("Updated")
                .countryId(1L)
                .stateOrProvinceId(1L)
                .districtId(1L)
                .build();

        Address address = new Address();
        address.setId(1L);
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        
        when(countryRepository.findById(1L)).thenReturn(Optional.of(new Country()));
        when(stateOrProvinceRepository.findById(1L)).thenReturn(Optional.of(new StateOrProvince()));
        when(districtRepository.findById(1L)).thenReturn(Optional.of(new District()));

        addressService.updateAddress(1L, dto);

        assertThat(address.getContactName()).isEqualTo("Updated");
        verify(addressRepository).save(address);
    }

    @Test
    void updateAddress_whenOptionalFieldsNotFound_saveAddress() {
        AddressPostVm dto = AddressPostVm.builder()
                .contactName("Updated")
                .countryId(1L)
                .stateOrProvinceId(1L)
                .districtId(1L)
                .build();

        Address address = new Address();
        address.setId(1L);
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        
        when(countryRepository.findById(1L)).thenReturn(Optional.empty());
        when(stateOrProvinceRepository.findById(1L)).thenReturn(Optional.empty());
        when(districtRepository.findById(1L)).thenReturn(Optional.empty());

        addressService.updateAddress(1L, dto);

        assertThat(address.getContactName()).isEqualTo("Updated");
        verify(addressRepository).save(address);
    }

    @Test
    void getAddressList_whenCalled_returnList() {
        Address address = Address.builder()
                .id(1L)
                .contactName("John")
                .country(Country.builder().id(1L).name("Country").build())
                .stateOrProvince(StateOrProvince.builder().id(1L).name("State").build())
                .district(District.builder().id(1L).name("District").build())
                .build();
        when(addressRepository.findAllByIdIn(any())).thenReturn(List.of(address));

        List<AddressDetailVm> result = addressService.getAddressList(List.of(1L));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).contactName()).isEqualTo("John");
    }

    @Test
    void getAddress_whenNotFound_throwNotFoundException() {
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.getAddress(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getAddress_whenValid_returnAddressDetailVm() {
        Address address = Address.builder()
                .id(1L)
                .contactName("John")
                .country(Country.builder().id(1L).name("Country").build())
                .stateOrProvince(StateOrProvince.builder().id(1L).name("State").build())
                .district(District.builder().id(1L).name("District").build())
                .build();
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));

        AddressDetailVm result = addressService.getAddress(1L);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void deleteAddress_whenNotFound_throwNotFoundException() {
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.deleteAddress(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteAddress_whenValid_deleteFromRepository() {
        Address address = new Address();
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));

        addressService.deleteAddress(1L);

        verify(addressRepository).delete(address);
    }
}
