package com.yas.cart.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.yas.cart.exception.BadRequestException;
import com.yas.cart.model.Cart;
import com.yas.cart.model.CartDetail;
import com.yas.cart.repository.CartDetailRepository;
import com.yas.cart.repository.CartRepository;
import com.yas.cart.viewmodel.*;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final ProductService productService;

    public CartService(CartRepository cartRepository, CartDetailRepository cartDetailRepository, ProductService productService) {
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
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

    public CartGetDetailVm createCart(CartPostVm cartPostVm) {
        Cart cart = new Cart();
        List<CartDetail> cartDetailList = new ArrayList<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        cart.setCustomerId(auth.getName());
        if (CollectionUtils.isNotEmpty(cartPostVm.cartItemVm())) {
            for (CartItemVm cartItemVm : cartPostVm.cartItemVm()) {
                if(cartItemVm.quantity() <= 0) 
                    throw new BadRequestException(("Quantity cannot be negative"));

                try {
                    productService.getProduct(cartItemVm.productId());
                } catch (Exception e) {
                    throw new BadRequestException(String.format("Not found product %d", cartItemVm.productId()));
                }
                
                CartDetail cartDetail = new CartDetail();
                cartDetail.setCart(cart);
                cartDetail.setProductId(cartItemVm.productId());
                cartDetail.setQuantity(cartItemVm.quantity());
                cartDetailList.add(cartDetail);
            }
            cart.setCartDetails(cartDetailList);
            cart.setCreatedBy(auth.getName());
            cart.setCreatedOn(ZonedDateTime.now());

            Cart savedCart = cartRepository.saveAndFlush(cart);
            cartDetailRepository.saveAllAndFlush(cartDetailList);
            return CartGetDetailVm.fromModel(savedCart);
        } else
            throw new BadRequestException("Cart's item can't be null");
    }
}
