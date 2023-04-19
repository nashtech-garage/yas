package com.yas.cart.service;

import com.yas.cart.exception.BadRequestException;
import com.yas.cart.exception.NotFoundException;
import com.yas.cart.model.Cart;
import com.yas.cart.model.CartItem;
import com.yas.cart.repository.CartItemRepository;
import com.yas.cart.repository.CartRepository;
import com.yas.cart.utils.Constants;
import com.yas.cart.viewmodel.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    private static final String CART_ITEM_UPDATED_MSG = "PRODUCT %s";

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
        // Call API to check all products will be added to cart are existed
        List<Long> productIds = cartItemVms.stream().map(CartItemVm::productId).toList();
        List<ProductThumbnailVm> productThumbnailVmList = productService.getProducts(productIds);
        if (productThumbnailVmList.size() != productIds.size()) {
            throw new NotFoundException(Constants.ERROR_CODE.NOT_FOUND_PRODUCT);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String customerId = auth.getName();

        Cart cart = cartRepository.findByCustomerIdAndOrderIdIsNull(customerId).stream().findFirst().orElse(null);
        Set<CartItem> existedCartItems = new HashSet<>();

        if (cart == null) {
            cart = Cart.builder()
                    .customerId(customerId)
                    .cartItems(existedCartItems)
                    .build();
            cart.setCreatedOn(ZonedDateTime.now());
        } else {
            existedCartItems = cartItemRepository.findAllByCart(cart);
        }

        for (CartItemVm cartItemVm : cartItemVms) {
            CartItem cartItem = getCartItemByProductId(existedCartItems, cartItemVm.productId());
            if (cartItem.getId() != null) {
                cartItem.setQuantity(cartItem.getQuantity() + cartItemVm.quantity());
            } else {
                cartItem.setCart(cart);
                cartItem.setProductId(cartItemVm.productId());
                cartItem.setQuantity(cartItemVm.quantity());
                cartItem.setParentProductId(cartItemVm.parentProductId());
                cart.getCartItems().add(cartItem);
            }
        }
        cart = cartRepository.save(cart);
        cartItemRepository.saveAll(cart.getCartItems());

        return CartGetDetailVm.fromModel(cart);
    }

    public CartGetDetailVm getLastCart() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return cartRepository.findByCustomerIdAndOrderIdIsNull(auth.getName())
                .stream().reduce((first, second) -> second)
                .map(CartGetDetailVm::fromModel).orElse(CartGetDetailVm.fromModel(new Cart()));
    }

    private CartItem getCartItemByProductId(Set<CartItem> cartItems, Long productId) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProductId().equals(productId))
                return cartItem;
        }
        return new CartItem();
    }

    public CartItemPutVm updateCartItems(CartItemVm cartItemVm) {
        CartGetDetailVm currentCart = getLastCart();

        validateCart(currentCart, cartItemVm.productId());

        Long cartId = currentCart.id();
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cartId, cartItemVm.productId())
                .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.NON_EXISTING_CART_ITEM + cartId));

        int newQuantity = cartItemVm.quantity();
        cartItem.setQuantity(newQuantity);
        if (newQuantity == 0) {
            cartItemRepository.delete(cartItem);
            return CartItemPutVm.fromModel(cartItem, String.format(CART_ITEM_UPDATED_MSG, "DELETED"));
        } else {
            CartItem savedCartItem = cartItemRepository.saveAndFlush(cartItem);
            cartItem.setQuantity(newQuantity);
            return CartItemPutVm.fromModel(savedCartItem, String.format(CART_ITEM_UPDATED_MSG, "UPDATED"));
        }
    }

    public ResponeStatusVm addOrderIdInToCart(Long orderId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String customerId = auth.getName();
        Cart cart = cartRepository.findByCustomerIdAndOrderIdIsNull(customerId).stream().findFirst()
                .orElseThrow(()
                        -> new NotFoundException(Constants.ERROR_CODE.NOT_FOUND_CART));
        cart.setOrderId(orderId);
        cartRepository.save(cart);
        return new ResponeStatusVm("Action success", "Action success", HttpStatus.OK.toString());
    }

    private void validateCart(CartGetDetailVm cart, Long productId) {
        if (cart.cartDetails().isEmpty()) {
            throw new BadRequestException(Constants.ERROR_CODE.NOT_EXISTING_ITEM_IN_CART);
        }

        List<CartDetailVm> cartDetailListVm = cart.cartDetails();
        boolean itemExist = cartDetailListVm.stream().anyMatch(item -> item.productId().equals(productId));
        if (!itemExist) {
            throw new NotFoundException(Constants.ERROR_CODE.NOT_EXISTING_PRODUCT_IN_CART, productId);
        }
    }

    @Transactional
    public void removeCartItemByProductId(Long productId) {
        CartGetDetailVm currentCart = getLastCart();

        validateCart(currentCart, productId);

        cartItemRepository.deleteByCartIdAndProductId(currentCart.id(), productId);
    }

    public Flux<Integer> countNumberItemInCart(String customerId) {
        return Flux.interval(Duration.ofSeconds(1)).map((i) -> {
            Optional<Cart> cartOp = cartRepository.findByCustomerIdAndOrderIdIsNull(customerId)
                    .stream().reduce((first, second) -> second);
            return cartOp.isPresent() ? cartItemRepository.countItemInCart(cartOp.get().getId()) : 0;
        });
    }
}
