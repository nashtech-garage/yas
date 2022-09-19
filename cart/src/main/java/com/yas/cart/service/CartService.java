package com.yas.cart.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.yas.cart.repository.CartDetailRepository;
import com.yas.cart.repository.CartRepository;
import com.yas.cart.viewmodel.*;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;

    public CartService(CartRepository cartRepository, CartDetailRepository cartDetailRepository) {
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
    }

    public List<CartListVM> getCarts() {
        return cartRepository.findAll()
                .stream().map(CartListVM::fromModel)
                .toList();
    }
    
    public List<CartGetDetailVM> getCartDetailByCustomerId(String customerId) {
        return cartRepository.findByCustomerId(customerId)
                .stream().map(CartGetDetailVM::fromModel)
                .toList();
    }
}
