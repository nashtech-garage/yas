package com.yas.customer.controller;

import com.yas.customer.service.CustomerService;
import com.yas.customer.viewmodel.CustomerVm;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/backoffice/customers")
    public ResponseEntity<List<CustomerVm>> getCustomers() {
        return ResponseEntity.ok(customerService.getCustomers());
    }
}
