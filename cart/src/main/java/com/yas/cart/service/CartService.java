package com.yas.cart.service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.yas.cart.exception.ApiExceptionHandler;
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

import static java.util.stream.Collectors.toList;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

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

        for (CartItemVm cartItemVm : cartItemVms) {
            if (cartItemVm.quantity() <= 0)
                throw new BadRequestException(("Quantity cannot be negative"));

            try {
                productService.getProduct(cartItemVm.productId());
            } catch (Exception e) {
                throw new BadRequestException(String.format("Not found product %d", cartItemVm.productId()));
            }

            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProductId(cartItemVm.productId());
            cartItem.setQuantity(cartItemVm.quantity());
            cart.getCartItems().add(cartItem);
            cartItemRepository.save(cartItem);
        }

        cartRepository.saveAndFlush(cart);
        return CartGetDetailVm.fromModel(cart);
    }

    public void updateCartItems(CartItemListVm cartItemListVm) {
//        List<Long> productIds = cartItemListVm.cartItemVmList().stream()
//                .map(cartItemVm -> cartItemVm.productId())
//                .toList();

        CartGetDetailVm cartGetDetailVm = getLastCart();
        List<CartDetailListVm> cartDetailListVm = cartGetDetailVm.cartDetails();
        Cart cart = cartRepository.findById(cartGetDetailVm.id())
                .orElseThrow(() -> new RuntimeException("Server error: There is no cart with ID: " + cartGetDetailVm.id()));

        if (cartDetailListVm.isEmpty()) {
            List<CartItem> cartItems = cartItemListVm.cartItemVmList()
                    .stream()
                    .map(item -> CartItem.fromVm(item, cart))
                    .toList();

            cartItemRepository.saveAllAndFlush(cartItems);
        } else {
            Set<CartItem> cartItemSet = cart.getCartItems();

            Map<Long, Integer> cartItemMap = cartItemListVm.cartItemVmList()
                    .stream()
                    .collect(Collectors.toMap(CartItemListVm::getProductId, CartItemListVm::getproductQuantity));
            System.out.println(cartItemMap);

            Map<Long, Integer> newCartItemMap = cartItemMap;

            for (CartItem cartItem : cartItemSet) {
                if (cartItemMap.containsKey(cartItem.getProductId())) {
                    cartItem.setQuantity(cartItemMap.get(cartItem.getProductId()));
                    newCartItemMap.remove(cartItem.getProductId());
                }
            }

            newCartItemMap.forEach((k, v) -> {
                System.out.println(k + "\t" + v);
            });

            cart.setCartItems(cartItemSet);
            cartRepository.saveAndFlush(cart);
        }
    }

    public void removeCartItem(Long productId) {
        try {
            productService.getProduct(productId);
        } catch (Exception e) {
            throw new NotFoundException(String.format("Product %s is not exist", productId));
        }

        CartGetDetailVm cartGetDetailVm = getLastCart();
        Cart cart = cartRepository.findById(cartGetDetailVm.id())
                .orElseThrow(() -> new RuntimeException("Server error: There is no cart with ID: " + cartGetDetailVm.id()));

        List<CartDetailListVm> cartDetailListVmList = cartGetDetailVm.cartDetails();
        if (cartDetailListVmList.isEmpty()) {
            throw new NotFoundException("There is no cart item in cart: " + cartGetDetailVm.id());
        }

        boolean itemExist = cartDetailListVmList.stream().anyMatch(item -> item.productId().equals(productId));
        if (!itemExist) {
            throw new NotFoundException(String.format("There is no product with ID: %s in the current cart", productId));
        }

        CartItem cartItem = cartItemRepository.findByCartAndProductId(cart, productId)
                .orElseThrow(() -> new NotFoundException(String.format("No cart item matching with product id %s and cart id %s", productId, cart.getId())));

        cartItemRepository.delete(cartItem);
    }

    public CartGetDetailVm getLastCart() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return cartRepository.findByCustomerId(auth.getName())
                .stream().reduce((first, second) -> second)
                .map(CartGetDetailVm::fromModel).orElse(null);
    }
}
