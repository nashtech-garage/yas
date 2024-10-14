package com.yas.cart.service;

import com.yas.cart.mapper.CartItemV2Mapper;
import com.yas.cart.model.CartItemV2;
import com.yas.cart.repository.CartItemV2Repository;
import com.yas.cart.utils.Constants;
import com.yas.cart.viewmodel.CartItemV2DeleteVm;
import com.yas.cart.viewmodel.CartItemV2GetVm;
import com.yas.cart.viewmodel.CartItemV2PostVm;
import com.yas.cart.viewmodel.CartItemV2PutVm;
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

    @Transactional
    public CartItemV2GetVm updateCartItem(Long productId, CartItemV2PutVm cartItemPutVm) {
        validateProduct(productId);

        String currentUserId = AuthenticationUtils.extractUserId();
        CartItemV2 cartItem = cartItemMapper.toCartItem(currentUserId, productId, cartItemPutVm.quantity());

        CartItemV2 savedCartItem = cartItemRepository.save(cartItem);
        return cartItemMapper.toGetVm(savedCartItem);
    }

    public List<CartItemV2GetVm> getCartItems() {
        String currentUserId = AuthenticationUtils.extractUserId();
        List<CartItemV2> cartItems = cartItemRepository.findByCustomerId(currentUserId);
        return cartItemMapper.toGetVmList(cartItems);
    }

    @Transactional
    public List<CartItemV2GetVm> deleteOrAdjustCartItem(List<CartItemV2DeleteVm> cartItemDeleteVms) {
        validateCartItemDeleteVms(cartItemDeleteVms);

        Map<Long, CartItemV2> cartItemById = getCartItemsByProductIds(cartItemDeleteVms);

        List<CartItemV2> cartItemsToDelete = new ArrayList<>();
        List<CartItemV2> cartItemsToAdjust = new ArrayList<>();

        for (CartItemV2DeleteVm cartItemDeleteVm : cartItemDeleteVms) {
            Optional<CartItemV2> optionalCartItem = Optional.ofNullable(cartItemById.get(cartItemDeleteVm.productId()));
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
        List<CartItemV2> updatedCartItems = cartItemRepository.saveAll(cartItemsToAdjust);

        return cartItemMapper.toGetVmList(updatedCartItems);
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

    private CartItemV2 performAddCartItem(CartItemV2PostVm cartItemPostVm, String currentUserId) {
        try {
            return cartItemRepository.findByCustomerIdAndProductId(currentUserId, cartItemPostVm.productId())
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

    private void validateCartItemDeleteVms(List<CartItemV2DeleteVm> cartItemDeleteVms) {
        Map<Long, Integer> quantityByProductId = new HashMap<>();

        for (CartItemV2DeleteVm cartItemDeleteVm : cartItemDeleteVms) {
            Integer existingQuantity = quantityByProductId.get(cartItemDeleteVm.productId());

            if (!Objects.isNull(existingQuantity) && !existingQuantity.equals(cartItemDeleteVm.quantity())) {
                throw new BadRequestException(Constants.ErrorCode.DUPLICATED_CART_ITEMS_TO_DELETE);
            }

            quantityByProductId.put(cartItemDeleteVm.productId(), cartItemDeleteVm.quantity());
        }
    }

    private Map<Long, CartItemV2> getCartItemsByProductIds(List<CartItemV2DeleteVm> cartItemDeleteVms) {
        String currentUserId = AuthenticationUtils.extractUserId();
        List<Long> productIds = cartItemDeleteVms
            .stream()
            .map(CartItemV2DeleteVm::productId)
            .toList();
        List<CartItemV2> cartItems = cartItemRepository.findByCustomerIdAndProductIdIn(currentUserId, productIds);
        return cartItems
            .stream()
            .collect(Collectors.toMap(CartItemV2::getProductId, Function.identity()));
    }
}