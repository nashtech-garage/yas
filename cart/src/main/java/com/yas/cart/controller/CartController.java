package com.yas.cart.controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.yas.cart.service.CartService;
import com.yas.cart.viewmodel.*;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
public class CartController {
    private CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/backoffice/carts")
    public ResponseEntity<List<CartListVM>> listCarts() {
        return ResponseEntity.ok(cartService.getCarts());
    }
    
    @GetMapping("/storefront/carts/{customerId}")
    public ResponseEntity<List<CartGetDetailVM>> listCartDetailByCustomerId(@PathVariable String customerId, Principal principal, HttpServletRequest request) {
        // Only admin or the owner of the cart can access.
        if(principal != null && (principal.getName().equals(customerId) || request.isUserInRole("ADMIN")))
            return ResponseEntity.ok(cartService.getCartDetailByCustomerId(customerId));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping(path = "/storefront/carts", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = CartGetDetailVM.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<CartGetDetailVM> createCart(@Valid @ModelAttribute CartPostVM cartPostVM, UriComponentsBuilder uriComponentsBuilder) {
        CartGetDetailVM cartGetDetailVM = cartService.createCart(cartPostVM);
        return ResponseEntity.created(uriComponentsBuilder.replacePath("/carts/{customerId}").buildAndExpand(cartGetDetailVM.customerId()).toUri())
                .body(cartGetDetailVM);
    }
}
