package com.yas.location.controller;

import com.yas.location.service.AddressService;
import com.yas.location.viewmodel.address.AddressGetVm;
import com.yas.location.viewmodel.address.AddressPostVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @PostMapping("/storefront/address")
    public ResponseEntity<AddressGetVm> createAddress(@RequestBody AddressPostVm dto) {
        return ResponseEntity.ok(addressService.createAddress(dto));
    }

    @PutMapping("/storefront/address/{id}")
    public ResponseEntity updateAddress(@PathVariable Long id, @RequestBody AddressPostVm dto) {
        addressService.updateAddress(id, dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/storefront/address/{id}")
    public ResponseEntity<AddressGetVm> getAddressById(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.getAddress(id));
    }

    @GetMapping("/storefront/addresses")
    public ResponseEntity<List<AddressGetVm>> getAddressList(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(addressService.getAddressList(ids));
    }

    @DeleteMapping("/storefront/address/{id}")
    public ResponseEntity deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok().build();
    }
}
