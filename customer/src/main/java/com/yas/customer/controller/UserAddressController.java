package com.yas.customer.controller;

import com.yas.customer.service.UserAddressService;
import com.yas.customer.viewmodel.address.AddressListVm;
import com.yas.customer.viewmodel.address.AddressPostVm;
import com.yas.customer.viewmodel.user_address.UserAddressVm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    @GetMapping("/storefront/user-address")
    public ResponseEntity<AddressListVm> getUserAddresses(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        return ResponseEntity.ok(userAddressService.getUserAddressList(pageNo, pageSize));
    }

    @PostMapping("/storefront/user-address")
    public ResponseEntity<UserAddressVm> createAddress(@Valid @RequestBody AddressPostVm addressPostVm) {
        return ResponseEntity.ok(userAddressService.createAddress(addressPostVm));
    }

    @DeleteMapping("/storefront/user-address/{id}")
    public ResponseEntity deleteAddress(@PathVariable Long id) {
        userAddressService.deleteAddress(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/storefront/user-address/{id}")
    public ResponseEntity chooseDefaultAddress(@PathVariable Long id) {
        userAddressService.chooseDefaultAddress(id);
        return ResponseEntity.ok().build();
    }
}
