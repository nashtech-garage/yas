package com.yas.location.service;

import com.yas.location.exception.NotFoundException;
import com.yas.location.mapper.AddressResponseMapper;
import com.yas.location.model.Address;
import com.yas.location.model.Country;
import com.yas.location.repository.AddressRepository;
import com.yas.location.repository.CountryRepository;
import com.yas.location.repository.DistrictRepository;
import com.yas.location.repository.StateOrProvinceRepository;
import com.yas.location.utils.Constants;
import com.yas.location.viewmodel.address.AddressGetVm;
import com.yas.location.viewmodel.address.AddressPostVm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final StateOrProvinceRepository stateOrProvinceRepository;
    private final CountryRepository countryRepository;
    private final DistrictRepository districtRepository;

    public Address createAddress(AddressPostVm dto) {
        Address address = AddressPostVm.fromModel(dto);
        stateOrProvinceRepository.findById(dto.stateOrProvinceId()).ifPresent(address::setStateOrProvince);
        Country country = countryRepository.findById(dto.countryId())
                .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.COUNTRY_NOT_FOUND, dto.countryId()));
        address.setCountry(country);
        districtRepository.findById(dto.districtId()).ifPresent(address::setDistrict);
        return addressRepository.save(address);
    }

    public void updateAddress(Long id, AddressPostVm dto) {
        Address address = addressRepository.findById(id).orElseThrow(() ->
                new NotFoundException(Constants.ERROR_CODE.ADDRESS_NOT_FOUND, id));

        address.setContactName(dto.contactName());
        address.setAddressLine1(dto.addressLine1());
        address.setPhone(dto.phone());
        address.setCity(dto.city());
        address.setZipCode(dto.zipCode());

        stateOrProvinceRepository.findById(dto.stateOrProvinceId()).ifPresent(address::setStateOrProvince);
        countryRepository.findById(dto.countryId()).ifPresent(address::setCountry);
        districtRepository.findById(dto.districtId()).ifPresent(address::setDistrict);
        addressRepository.saveAndFlush(address);
    }

    public List<AddressResponseMapper> getAddressList(List<Long> ids) {
        return addressRepository.findAllByIdIn(ids);
    }

    public AddressGetVm getAddress(Long id) {
        Address address = addressRepository.findById(id).orElseThrow(() ->
                new NotFoundException(Constants.ERROR_CODE.ADDRESS_NOT_FOUND, id));
        return AddressGetVm.fromModel(address);
    }

    public void deleteAddress(Long id) {
        Address address = addressRepository.findById(id).orElseThrow(() ->
                new NotFoundException(Constants.ERROR_CODE.ADDRESS_NOT_FOUND, id));
        addressRepository.delete(address);
    }
}
