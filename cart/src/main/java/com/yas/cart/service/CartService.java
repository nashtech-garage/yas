package com.yas.cart.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.yas.cart.exception.BadRequestException;
import com.yas.cart.model.Cart;
import com.yas.cart.model.CartItem;
import com.yas.cart.repository.CartItemRepository;
import com.yas.cart.repository.CartRepository;
import com.yas.cart.viewmodel.*;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductService productService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
    }

    public List<CartListVm> getCarts() {
        return cartRepository.findAll()
                .stream().map(CartListVm::fromModel)
                .toList();
    }
    
    public List<CartGetDetailVm> getCartDetailByCustomerId(String customerId) {
        return cartRepository.findByCustomerId(customerId)
                .stream().map(CartGetDetailVm::fromModel)
                .toList();
    }

    public CartGetDetailVm addToCart(List<CartItemVm> cartItemVms) {
        if (CollectionUtils.isEmpty(cartItemVms)) {
            throw new BadRequestException("Cart's item can't be null");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String customerId = auth.getName();

        Cart cart = cartRepository.findByCustomerId(customerId).stream().findFirst().orElse(null);
        if (cart == null) {
            cart = new Cart(null, customerId, new HashSet<>());
            cart.setCreatedBy(auth.getName());
            cart.setCreatedOn(ZonedDateTime.now());
            cartRepository.save(cart);
        }

        List<CartItem> existedCartItems = cartItemRepository.findAllByCart(cart);
        for (CartItemVm cartItemVm : cartItemVms) {
            if (cartItemVm.quantity() <= 0)
                throw new BadRequestException(("Quantity cannot be negative"));

            try {
                productService.getProduct(cartItemVm.productId());
            } catch (Exception e) {
                throw new BadRequestException(String.format("Not found product %d", cartItemVm.productId()));
            }

            if (getCartItemByProductId(existedCartItems, cartItemVm.productId()) != null) {
                CartItem existedCartItem = getCartItemByProductId(existedCartItems, cartItemVm.productId());
                existedCartItem.setQuantity(existedCartItem.getQuantity() + cartItemVm.quantity());
                cartItemRepository.save(existedCartItem);
            } else {
                CartItem cartItem = new CartItem();
                cartItem.setCart(cart);
                cartItem.setProductId(cartItemVm.productId());
                cartItem.setQuantity(cartItemVm.quantity());
                cart.getCartItems().add(cartItem);
                cartItemRepository.save(cartItem);
                cartRepository.saveAndFlush(cart);
            }
        }

        return CartGetDetailVm.fromModel(cart);
    }

    public CartGetDetailVm getLastCart() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return cartRepository.findByCustomerId(auth.getName())
                .stream().reduce((first, second) -> second)
                .map(CartGetDetailVm::fromModel).orElse(null);
    }

    private CartItem getCartItemByProductId(List<CartItem> cartItems, Long productId) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProductId().equals(productId))
                return cartItem;
        }
        return null;
    }
}
