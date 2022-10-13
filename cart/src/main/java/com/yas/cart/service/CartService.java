package com.yas.cart.service;

import java.time.ZonedDateTime;
import java.util.*;

import com.yas.cart.exception.NotFoundException;
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

    private final String CART_ITEM_UPDATED_MSG = "Updated";

    private final String CART_ITEM_DELETED_MSG = "Deleted";

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

            CartItem cartItem = getCartItemByProductId(existedCartItems, cartItemVm.productId());
            if (cartItem.getId() != null) {
                cartItem.setQuantity(cartItem.getQuantity() + cartItemVm.quantity());
                cartItemRepository.save(cartItem);
            } else {
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

    public CartItemPutVm updateCartItems(CartItemVm cartItemVm) {
        CartGetDetailVm currentCart = getLastCart();

        if(currentCart.cartDetails().isEmpty()) {
            throw new BadRequestException("There is no cart item in current cart to update !");
        }

        List<CartDetailListVm> cartDetailListVmList = currentCart.cartDetails();
        boolean itemExist = cartDetailListVmList.stream().anyMatch(item -> item.productId().equals(cartItemVm.productId()));
        if (!itemExist) {
            throw new NotFoundException(String.format("There is no product with ID: %s in the current cart", cartItemVm.productId()));
        }

        Long cartId = currentCart.id();
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cartId, cartItemVm.productId())
                .orElseThrow(() -> new NotFoundException("Non exist cart item with ID: " + cartId));
        cartItem.setQuantity(cartItemVm.quantity());
        CartItem savedCartItem = cartItemRepository.saveAndFlush(cartItem);
        return CartItemPutVm.fromModel(savedCartItem, CART_ITEM_UPDATED_MSG);
    }

    public CartItemDeleteVm removeCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundException("Non exist cart item with ID: " + cartItemId));

        CartGetDetailVm currentCart = getLastCart();
        List<CartDetailListVm> cartDetailListVmList = currentCart.cartDetails();
        if (cartDetailListVmList.isEmpty()) {
            throw new NotFoundException("There is no cart item in cart: " + cartItemId);
        }
        boolean itemExist = cartDetailListVmList.stream().anyMatch(item -> item.id().equals(cartItemId));
        if (!itemExist) {
            throw new NotFoundException(String.format("There is no cart item with ID: %s in the current cart", cartItemId));
        }
        cartItemRepository.delete(cartItem);
        return CartItemDeleteVm.fromModel(cartItem, CART_ITEM_DELETED_MSG);
    }

    public CartGetDetailVm getLastCart() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return cartRepository.findByCustomerId(auth.getName())
                .stream().reduce((first, second) -> second)
                .map(CartGetDetailVm::fromModel).orElse(CartGetDetailVm.fromModel(new Cart()));
    }

    private CartItem getCartItemByProductId(List<CartItem> cartItems, Long productId) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProductId().equals(productId))
                return cartItem;
        }
        return new CartItem();
    }
}
