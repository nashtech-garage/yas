package com.yas.location.controller;

import com.yas.location.mapper.AddressResponseMapper;
import com.yas.location.service.AddressService;
import com.yas.location.viewmodel.address.AddressGetVm;
import com.yas.location.viewmodel.address.AddressPostVm;
import com.yas.location.viewmodel.address.RequestAddressGetListVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @PostMapping("/storefront/address")
    public ResponseEntity<Long> createAddress(@RequestBody AddressPostVm dto) {
        return ResponseEntity.ok(addressService.createAddress(dto).getId());
    }

    @PutMapping("/storefront/address/{id}")
    public ResponseEntity updateAddress(@PathVariable Long id, @RequestBody AddressPostVm dto) {
        addressService.updateAddress(id, dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/storefront/address/{id}")
    public ResponseEntity<AddressGetVm> getAddress(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.getAddress(id));
    }

    @PostMapping("/storefront/addresses")
    public ResponseEntity<List<AddressResponseMapper>> getAddressList(@RequestBody RequestAddressGetListVm dto) {
        return ResponseEntity.ok(addressService.getAddressList(dto.ids()));
    }

    @DeleteMapping("/storefront/address/{id}")
    public ResponseEntity deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok().build();
    }
}
