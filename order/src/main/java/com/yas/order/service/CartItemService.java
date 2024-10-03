package com.yas.order.service;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.mapper.CartItemMapper;
import com.yas.order.model.CartItem;
import com.yas.order.model.CartItemId;
import com.yas.order.repository.CartItemRepository;
import com.yas.order.utils.AuthenticationUtils;
import com.yas.order.utils.Constants;
import com.yas.order.viewmodel.cart.CartItemPostVm;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    public void addCartItem(CartItemPostVm cartItemPostVm) {
        validateCartItemPostVm(cartItemPostVm);

        String currentUserId = AuthenticationUtils.getCurrentUserId();
        CartItemId cartItemId = CartItemId.of(currentUserId, cartItemPostVm.productId());
        Optional<CartItem> existingCartItemOpt = cartItemRepository.findById(cartItemId);

        if (existingCartItemOpt.isPresent()) {
            updateExistingCartItem(cartItemPostVm, existingCartItemOpt.get());
        } else {
            createNewCartItem(cartItemPostVm, currentUserId);
        }
    }

    private void createNewCartItem(CartItemPostVm cartItemPostVm, String currentUserId) {
        CartItem cartItem = CartItem
            .builder()
            .customerId(currentUserId)
            .productId(cartItemPostVm.productId())
            .quantity(cartItemPostVm.quantity())
            .build();
        cartItemRepository.save(cartItem);
    }

    private void updateExistingCartItem(CartItemPostVm cartItemPostVm, CartItem existingCartItem) {
        existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemPostVm.quantity());
        cartItemRepository.save(existingCartItem);
    }

    private void validateCartItemPostVm(CartItemPostVm cartItemPostVm) {
        try {
            productService.getProductById(cartItemPostVm.productId());
        } catch (NotFoundException e) {
            throw new BadRequestException(Constants.ErrorCode.PRODUCT_NOT_FOUND, cartItemPostVm.productId());
        }
    }
}
