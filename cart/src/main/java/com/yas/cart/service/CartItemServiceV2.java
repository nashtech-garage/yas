package com.yas.cart.service;

import com.yas.cart.mapper.CartItemMapperV2;
import com.yas.cart.model.CartItemIdV2;
import com.yas.cart.model.CartItemV2;
import com.yas.cart.repository.CartItemRepositoryV2;
import com.yas.cart.utils.Constants;
import com.yas.cart.viewmodel.CartItemGetVmV2;
import com.yas.cart.viewmodel.CartItemPostVmV2;
import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.utils.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemServiceV2 {
    private final CartItemRepositoryV2 cartItemRepository;
    private final ProductService productService;
    private final CartItemMapperV2 cartItemMapper;

    public CartItemGetVmV2 addCartItem(CartItemPostVmV2 cartItemPostVm) {
        validateProduct(cartItemPostVm.productId());

        String currentUserId = AuthenticationUtils.extractUserId();
        CartItemIdV2 cartItemId = CartItemIdV2.of(currentUserId, cartItemPostVm.productId());

        CartItemV2 cartItem = cartItemRepository.findByIdWithLock(cartItemId)
            .map(existingCartItem -> updateExistingCartItem(cartItemPostVm, existingCartItem))
            .orElseGet(() -> createNewCartItem(cartItemPostVm, currentUserId));

        return cartItemMapper.toGetVm(cartItem);
    }

    private void validateProduct(Long productId) {
        try {
            productService.getProductById(productId);
        } catch (NotFoundException e) {
            throw new BadRequestException(Constants.ErrorCode.NOT_FOUND_PRODUCT, productId);
        }
    }

    private CartItemV2 createNewCartItem(CartItemPostVmV2 cartItemPostVm, String currentUserId) {
        CartItemV2 cartItem = cartItemMapper.toCartItem(cartItemPostVm, currentUserId);
        return cartItemRepository.save(cartItem);
    }

    private CartItemV2 updateExistingCartItem(CartItemPostVmV2 cartItemPostVm, CartItemV2 existingCartItem) {
        existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemPostVm.quantity());
        return cartItemRepository.save(existingCartItem);
    }
}
