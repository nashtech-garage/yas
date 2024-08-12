package com.yas.cart.controller;

import com.yas.cart.service.CartService;
import com.yas.cart.viewmodel.CartGetDetailVm;
import com.yas.cart.viewmodel.CartItemPutVm;
import com.yas.cart.viewmodel.CartItemVm;
import com.yas.cart.viewmodel.CartListVm;
import com.yas.cart.viewmodel.ErrorVm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/backoffice/carts")
    public ResponseEntity<List<CartListVm>> listCarts() {
        return ResponseEntity.ok(cartService.getCarts());
    }

    @GetMapping("/backoffice/carts/{customerId}")
    public ResponseEntity<List<CartGetDetailVm>> listCartDetailByCustomerId(@PathVariable String customerId) {
        return ResponseEntity.ok(cartService.getCartDetailByCustomerId(customerId));
    }

    @GetMapping("/storefront/carts")
    public ResponseEntity<CartGetDetailVm> getLastCart(Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(cartService.getLastCart(principal.getName()));
    }

    @PostMapping(path = "/storefront/carts")
    @Operation(summary = "Add product to shopping cart. When no cart exists, this will create a new cart.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Add to cart successfully",
            content = @Content(schema = @Schema(implementation = CartGetDetailVm.class))),
        @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<CartGetDetailVm> createCart(@Valid @RequestBody @NotEmpty List<CartItemVm> cartItemVms) {
        CartGetDetailVm cartGetDetailVm = cartService.addToCart(cartItemVms);
        return new ResponseEntity<>(cartGetDetailVm, HttpStatus.CREATED);
    }

    @PutMapping("cart-item")
    public ResponseEntity<CartItemPutVm> updateCart(@Valid @RequestBody CartItemVm cartItemVm) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return new ResponseEntity<>(cartService.updateCartItems(cartItemVm, auth.getName()), HttpStatus.OK);
    }

    @DeleteMapping("/storefront/cart-item")
    public ResponseEntity<Void> removeCartItemByProductId(@RequestParam Long productId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        cartService.removeCartItemByProductId(productId, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/storefront/cart-item/multi-delete")
    public ResponseEntity<Void> removeCartItemListByProductIdList(@RequestParam List<Long> productIds) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        cartService.removeCartItemListByProductIdList(productIds, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/storefront/count-cart-items")
    public ResponseEntity<Long> getNumberItemInCart(Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok().body(0L);
        }
        return ResponseEntity.ok().body(cartService.countNumberItemInCart(principal.getName()));
    }

}
