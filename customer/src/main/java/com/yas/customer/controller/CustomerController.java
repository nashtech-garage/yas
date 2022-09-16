package com.yas.customer.controller;

import com.yas.customer.service.CustomerService;
import com.yas.customer.viewmodel.CustomerVm;
import com.yas.customer.viewmodel.ErrorVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "Access Denied", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<List<CustomerVm>> getCustomers() {
        return ResponseEntity.ok(customerService.getCustomers());
    }
}
