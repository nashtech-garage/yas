package com.yas.cart.service;

import com.yas.cart.mapper.CartItemV2Mapper;
import com.yas.cart.model.CartItemV2;
import com.yas.cart.repository.CartItemV2Repository;
import com.yas.cart.utils.Constants;
import com.yas.cart.viewmodel.CartItemV2GetVm;
import com.yas.cart.viewmodel.CartItemV2PostVm;
import com.yas.commonlibrary.exception.InternalServerErrorException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.utils.AuthenticationUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartItemV2Service {
    private final CartItemV2Repository cartItemRepository;
    private final ProductService productService;
    private final CartItemV2Mapper cartItemMapper;

    @Transactional
    public CartItemV2GetVm addCartItem(CartItemV2PostVm cartItemPostVm) {
        validateProduct(cartItemPostVm.productId());

        String currentUserId = AuthenticationUtils.extractUserId();
        CartItemV2 cartItem = performAddCartItem(cartItemPostVm, currentUserId);

        return cartItemMapper.toGetVm(cartItem);
    }

    private void validateProduct(Long productId) {
        if (!productService.existsById(productId)) {
            throw new NotFoundException(Constants.ErrorCode.NOT_FOUND_PRODUCT);
        }
    }
    
    @Transactional
    public CartItemV2GetVm updateCartItem(Long productId, CartItemV2PutVm cartItemPutVm) {
        validateProduct(productId);

        String currentUserId = AuthenticationUtils.extractUserId();
        CartItemV2 cartItemV2 = cartItemMapper.toCartItem(currentUserId, productId, cartItemPutVm.quantity());

        CartItemV2 savedCartItem = cartItemRepository.save(cartItemV2);
        return cartItemMapper.toGetVm(savedCartItem);
    }

    public List<CartItemV2GetVm> getCartItems() {
        String currentUserId = AuthenticationUtils.extractUserId();
        return cartItemRepository.findByCustomerId(currentUserId)
            .stream().map(cartItemMapper::toGetVm).toList();
    }

    private CartItemV2 performAddCartItem(CartItemV2PostVm cartItemPostVm, String currentUserId) {
        try {
            return cartItemRepository.findWithLock(currentUserId, cartItemPostVm.productId())
                .map(existingCartItem -> updateExistingCartItem(cartItemPostVm, existingCartItem))
                .orElseGet(() -> createNewCartItem(cartItemPostVm, currentUserId));
        } catch (PessimisticLockingFailureException e) {
            log.error("Failed to acquire lock for adding cart item", e);
            throw new InternalServerErrorException(Constants.ErrorCode.ADD_CART_ITEM_FAILED);
        }
    }

    private CartItemV2 createNewCartItem(CartItemV2PostVm cartItemPostVm, String currentUserId) {
        CartItemV2 cartItem = cartItemMapper.toCartItem(cartItemPostVm, currentUserId);
        return cartItemRepository.save(cartItem);
    }

    private CartItemV2 updateExistingCartItem(CartItemV2PostVm cartItemPostVm, CartItemV2 existingCartItem) {
        existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemPostVm.quantity());
        return cartItemRepository.save(existingCartItem);
    }
}
