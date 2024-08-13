package com.yas.customer.controller;

import com.yas.customer.service.CustomerService;
import com.yas.customer.viewmodel.ErrorVm;
import com.yas.customer.viewmodel.customer.CustomerAdminVm;
import com.yas.customer.viewmodel.customer.CustomerListVm;
import com.yas.customer.viewmodel.customer.CustomerProfileRequestVm;
import com.yas.customer.viewmodel.customer.CustomerVm;
import com.yas.customer.viewmodel.customer.GuestUserVm;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/backoffice/customers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "403", description = "Access Denied",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<CustomerListVm> getCustomers(
        @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo) {
        return ResponseEntity.ok(customerService.getCustomers(pageNo));
    }

    @GetMapping("/backoffice/customers/{email}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok",
            content = @Content(schema = @Schema(implementation = CustomerAdminVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = "403", description = "Access Denied",
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<CustomerAdminVm> getCustomerByEmail(@PathVariable String email) {
        return ResponseEntity.ok(customerService.getCustomerByEmail(email));
    }

    @GetMapping("/storefront/customer/profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok",
            content = @Content(schema = @Schema(implementation = CustomerVm.class))),
        @ApiResponse(responseCode = "403", description = "Access Denied",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<CustomerVm> getCustomerProfile() {
        return ResponseEntity.ok(
            customerService.getCustomerProfile(SecurityContextHolder.getContext().getAuthentication().getName()));
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created",
            content = @Content(schema = @Schema(implementation = GuestUserVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    @PostMapping("/storefront/customer/guest-user")
    public GuestUserVm createGuestUser() {
        return customerService.createGuestUser();
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Updated"),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))
    })
    @PutMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateProfile(@RequestBody CustomerProfileRequestVm requestVm) {
        customerService.updateCustomers(requestVm);
        return ResponseEntity.noContent().build();
    }
}
