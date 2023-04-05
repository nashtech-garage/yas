package com.yas.customer.controller;

import com.yas.customer.service.UserAddressService;
import com.yas.customer.viewmodel.AddressGetVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    @GetMapping("/storefront/user-address")
    public ResponseEntity<List<AddressGetVm>> getUserAddresses() {
        return ResponseEntity.ok(userAddressService.getUserAddressList());
    }

    @PostMapping("/storefront/user-address/{id}")
    public ResponseEntity createAddress(@PathVariable Long id) {
        userAddressService.createAddress(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/storefront/user-address/{id}")
    public ResponseEntity deleteAddress(@PathVariable Long id) {
        userAddressService.deleteAddress(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/storefront/user-address/{id}")
    public ResponseEntity chooseDefaultAddress(@PathVariable Long id) {
        userAddressService.chooseDefaultAddress(id);
        return ResponseEntity.noContent().build();
    }
}
