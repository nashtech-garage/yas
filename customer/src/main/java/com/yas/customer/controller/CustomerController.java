package com.yas.customer.controller;

import com.yas.customer.service.CustomerService;
import com.yas.customer.viewmodel.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<List<CustomerAdminVm>> getCustomers() {
        return ResponseEntity.ok(customerService.getCustomers());
    }

    @GetMapping("/backoffice/customers/{email}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = CustomerAdminVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = "403", description = "Access Denied", content = @Content(schema = @Schema(implementation = ErrorVm.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<CustomerAdminVm> getCustomerByEmail(@PathVariable String email) {
        return ResponseEntity.ok(customerService.getCustomerByEmail(email));
    }

    @GetMapping("/storefront/customer/profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = CustomerVm.class))),
            @ApiResponse(responseCode = "403", description = "Access Denied", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<CustomerVm> getCustomerProfile() {
        return ResponseEntity.ok(customerService.getCustomerProfile(SecurityContextHolder.getContext().getAuthentication().getName()));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = GuestUserVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    @GetMapping("/storefront/customer/guest-user")
    public GuestUserVm createGuestUser() {
        return customerService.createGuestUser();
    }
}
