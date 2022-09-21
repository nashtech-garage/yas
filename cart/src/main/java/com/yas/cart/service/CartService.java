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

    public CartGetDetailVM createCart(CartPostVM cartPostVM) {
        Cart cart = new Cart();
        List<CartDetail> cartDetailList = new ArrayList<>();

        cart.setCustomerId(cartPostVM.customerId());
        if (CollectionUtils.isNotEmpty(cartPostVM.CartDetailPostVMs())) {
            for (CartDetailPostVM cartDetailPostVM : cartPostVM.CartDetailPostVMs()) {
                try {
                    productService.getProduct(cartDetailPostVM.productId());
                } catch (Exception e) {
                    throw new BadRequestException(String.format("Not found product %d", cartDetailPostVM.productId()));
                }
                
                if(cartDetailPostVM.quantity() <= 0) 
                    throw new BadRequestException(("Quantity cannot be negative"));
                
                CartDetail cartDetail = new CartDetail();
                cartDetail.setCart(cart);
                cartDetail.setProductId(cartDetailPostVM.productId());
                cartDetail.setQuantity(cartDetailPostVM.quantity());
                cartDetailList.add(cartDetail);
            }
            cart.setCartDetails(cartDetailList);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            cart.setCreatedBy(auth.getName());
            cart.setCreatedOn(ZonedDateTime.now());

            Cart savedCart = cartRepository.saveAndFlush(cart);
            cartDetailRepository.saveAllAndFlush(cartDetailList);
            return CartGetDetailVM.fromModel(savedCart);
        } else
            throw new BadRequestException("Cart's detail can't be null");
    }
}
