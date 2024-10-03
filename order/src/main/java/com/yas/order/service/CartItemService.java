package com.yas.order.service;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.mapper.CartItemMapper;
import com.yas.order.model.CartItem;
import com.yas.order.model.CartItemId;
import com.yas.order.repository.CartItemRepository;
import com.yas.order.utils.AuthenticationUtils;
import com.yas.order.utils.Constants;
import com.yas.order.viewmodel.cart.CartItemGetVm;
import com.yas.order.viewmodel.cart.CartItemPostVm;
import com.yas.order.viewmodel.product.ProductThumbnailGetVm;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final ProductService productService;

    public List<CartItemGetVm> getCartItems() {
        List<CartItem> cartItems = cartItemRepository.findAll();
        return cartItems.stream().map(cartItemMapper::toGetVm).toList();
    }

    public void addCartItem(CartItemPostVm cartItemPostVm) {
        if (cartItemPostVm.quantity() <= 0) {
            throw new IllegalArgumentException(Constants.ErrorCode.NEGATIVE_CART_ITEM_QUANTITY);
        }

        ProductThumbnailGetVm productThumbnailGetVm;
        try {
            productThumbnailGetVm = productService.getProductById(cartItemPostVm.productId());
        } catch (NotFoundException e) {
            throw new IllegalArgumentException(Constants.ErrorCode.PRODUCT_NOT_FOUND);
        }

        String currentUserId = AuthenticationUtils.getCurrentUserId();
        CartItemId cartItemId = CartItemId.of(currentUserId, productThumbnailGetVm.id());
        Optional<CartItem> existingCartItemOpt = cartItemRepository.findById(cartItemId);

        if (existingCartItemOpt.isPresent()) {
            CartItem existingCartItem = existingCartItemOpt.get();
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemPostVm.quantity());
            cartItemRepository.save(existingCartItem);
        } else {
            CartItem cartItem = CartItem
                .builder()
                .customerId(currentUserId)
                .productId(productThumbnailGetVm.id())
                .quantity(cartItemPostVm.quantity())
                .build();
            cartItemRepository.save(cartItem);
        }
    }
}
