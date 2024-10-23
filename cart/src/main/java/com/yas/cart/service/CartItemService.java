package com.yas.cart.service;

import com.yas.cart.mapper.CartItemMapper;
import com.yas.cart.model.CartItem;
import com.yas.cart.repository.CartItemRepository;
import com.yas.cart.utils.Constants;
import com.yas.cart.viewmodel.CartItemDeleteVm;
import com.yas.cart.viewmodel.CartItemGetVm;
import com.yas.cart.viewmodel.CartItemPostVm;
import com.yas.cart.viewmodel.CartItemPutVm;
import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.InternalServerErrorException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.utils.AuthenticationUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final CartItemMapper cartItemMapper;

    @Transactional
    public CartItemGetVm addCartItem(CartItemPostVm cartItemPostVm) {
        validateProduct(cartItemPostVm.productId());

        String currentUserId = AuthenticationUtils.extractUserId();
        CartItem cartItem = performAddCartItem(cartItemPostVm, currentUserId);

        return cartItemMapper.toGetVm(cartItem);
    }

    @Transactional
    public CartItemGetVm updateCartItem(Long productId, CartItemPutVm cartItemPutVm) {
        validateProduct(productId);

        String currentUserId = AuthenticationUtils.extractUserId();
        CartItem cartItem = cartItemMapper.toCartItem(currentUserId, productId, cartItemPutVm.quantity());

        CartItem savedCartItem = cartItemRepository.save(cartItem);
        return cartItemMapper.toGetVm(savedCartItem);
    }

    public List<CartItemGetVm> getCartItems() {
        String currentUserId = AuthenticationUtils.extractUserId();
        List<CartItem> cartItems = cartItemRepository.findByCustomerIdOrderByCreatedOnDesc(currentUserId);
        return cartItemMapper.toGetVms(cartItems);
    }

    @Transactional
    public List<CartItemGetVm> deleteOrAdjustCartItem(List<CartItemDeleteVm> cartItemDeleteVms) {
        validateCartItemDeleteVms(cartItemDeleteVms);

        Map<Long, CartItem> cartItemById = getCartItemsByProductIds(cartItemDeleteVms);

        List<CartItem> cartItemsToDelete = new ArrayList<>();
        List<CartItem> cartItemsToAdjust = new ArrayList<>();

        for (CartItemDeleteVm cartItemDeleteVm : cartItemDeleteVms) {
            Optional<CartItem> optionalCartItem = Optional.ofNullable(cartItemById.get(cartItemDeleteVm.productId()));
            optionalCartItem.ifPresent(cartItem -> {
                if (cartItem.getQuantity() <= cartItemDeleteVm.quantity()) {
                    cartItemsToDelete.add(cartItem);
                } else {
                    cartItem.setQuantity(cartItem.getQuantity() - cartItemDeleteVm.quantity());
                    cartItemsToAdjust.add(cartItem);
                }
            });
        }

        cartItemRepository.deleteAll(cartItemsToDelete);
        List<CartItem> updatedCartItems = cartItemRepository.saveAll(cartItemsToAdjust);

        return cartItemMapper.toGetVms(updatedCartItems);
    }

    @Transactional
    public void deleteCartItem(Long productId) {
        String currentUserId = AuthenticationUtils.extractUserId();
        cartItemRepository.deleteByCustomerIdAndProductId(currentUserId, productId);
    }

    private void validateProduct(Long productId) {
        if (!productService.existsById(productId)) {
            throw new NotFoundException(Constants.ErrorCode.NOT_FOUND_PRODUCT, productId);
        }
    }

    private CartItem performAddCartItem(CartItemPostVm cartItemPostVm, String currentUserId) {
        try {
            return cartItemRepository.findByCustomerIdAndProductId(currentUserId, cartItemPostVm.productId())
                .map(existingCartItem -> updateExistingCartItem(cartItemPostVm, existingCartItem))
                .orElseGet(() -> createNewCartItem(cartItemPostVm, currentUserId));
        } catch (PessimisticLockingFailureException e) {
            log.error("Failed to acquire lock for adding cart item", e);
            throw new InternalServerErrorException(Constants.ErrorCode.ADD_CART_ITEM_FAILED);
        }
    }

    private CartItem createNewCartItem(CartItemPostVm cartItemPostVm, String currentUserId) {
        CartItem cartItem = cartItemMapper.toCartItem(cartItemPostVm, currentUserId);
        return cartItemRepository.save(cartItem);
    }

    private CartItem updateExistingCartItem(CartItemPostVm cartItemPostVm, CartItem existingCartItem) {
        existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemPostVm.quantity());
        return cartItemRepository.save(existingCartItem);
    }

    private void validateCartItemDeleteVms(List<CartItemDeleteVm> cartItemDeleteVms) {
        Map<Long, Integer> quantityByProductId = new HashMap<>();

        for (CartItemDeleteVm cartItemDeleteVm : cartItemDeleteVms) {
            Integer existingQuantity = quantityByProductId.get(cartItemDeleteVm.productId());

            if (!Objects.isNull(existingQuantity) && !existingQuantity.equals(cartItemDeleteVm.quantity())) {
                throw new BadRequestException(Constants.ErrorCode.DUPLICATED_CART_ITEMS_TO_DELETE);
            }

            quantityByProductId.put(cartItemDeleteVm.productId(), cartItemDeleteVm.quantity());
        }
    }

    private Map<Long, CartItem> getCartItemsByProductIds(List<CartItemDeleteVm> cartItemDeleteVms) {
        String currentUserId = AuthenticationUtils.extractUserId();
        List<Long> productIds = cartItemDeleteVms
            .stream()
            .map(CartItemDeleteVm::productId)
            .toList();
        List<CartItem> cartItems = cartItemRepository.findByCustomerIdAndProductIdIn(currentUserId, productIds);
        return cartItems
            .stream()
            .collect(Collectors.toMap(CartItem::getProductId, Function.identity()));
    }
}