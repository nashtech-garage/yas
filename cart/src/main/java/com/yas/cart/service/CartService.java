package com.yas.cart.service;

import com.yas.cart.exception.BadRequestException;
import com.yas.cart.exception.NotFoundException;
import com.yas.cart.model.Cart;
import com.yas.cart.model.CartItem;
import com.yas.cart.repository.CartItemRepository;
import com.yas.cart.repository.CartRepository;
import com.yas.cart.utils.Constants;
import com.yas.cart.viewmodel.CartDetailVm;
import com.yas.cart.viewmodel.CartGetDetailVm;
import com.yas.cart.viewmodel.CartItemPutVm;
import com.yas.cart.viewmodel.CartItemVm;
import com.yas.cart.viewmodel.CartListVm;
import com.yas.cart.viewmodel.ProductThumbnailVm;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
    private static final String CART_ITEM_UPDATED_MSG = "PRODUCT %s";
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                       ProductService productService) {
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
            throw new NotFoundException(Constants.ErrorCode.NOT_FOUND_PRODUCT, productIds);
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

    public CartGetDetailVm getLastCart(String customerId) {
        return cartRepository.findByCustomerIdAndOrderIdIsNull(customerId)
            .stream().reduce((first, second) -> second)
            .map(CartGetDetailVm::fromModel).orElse(CartGetDetailVm.fromModel(new Cart()));
    }

    private CartItem getCartItemByProductId(Set<CartItem> cartItems, Long productId) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProductId().equals(productId)) {
                return cartItem;
            }
        }
        return new CartItem();
    }

    public CartItemPutVm updateCartItems(CartItemVm cartItemVm, String customerId) {
        CartGetDetailVm currentCart = getLastCart(customerId);

        validateCart(currentCart, cartItemVm.productId());

        Long cartId = currentCart.id();
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cartId, cartItemVm.productId())
            .orElseThrow(() -> new NotFoundException(Constants.ErrorCode.NON_EXISTING_CART_ITEM + cartId));

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

    public void removeCartItemListByProductIdList(List<Long> productIdList, String customerId) {
        CartGetDetailVm currentCart = getLastCart(customerId);
        productIdList.forEach(id -> validateCart(currentCart, id));
        cartItemRepository.deleteByCartIdAndProductIdIn(currentCart.id(), productIdList);
    }

    private void validateCart(CartGetDetailVm cart, Long productId) {
        if (cart.cartDetails().isEmpty()) {
            throw new BadRequestException(Constants.ErrorCode.NOT_EXISTING_ITEM_IN_CART);
        }

        List<CartDetailVm> cartDetailListVm = cart.cartDetails();
        boolean itemExist = cartDetailListVm.stream().anyMatch(item -> item.productId().equals(productId));
        if (!itemExist) {
            throw new NotFoundException(Constants.ErrorCode.NOT_EXISTING_PRODUCT_IN_CART, productId);
        }
    }

    @Transactional
    public void removeCartItemByProductId(Long productId, String customerId) {
        CartGetDetailVm currentCart = getLastCart(customerId);

        validateCart(currentCart, productId);

        cartItemRepository.deleteByCartIdAndProductId(currentCart.id(), productId);
    }

    public Long countNumberItemInCart(String customerId) {
        Optional<Cart> cartOp = cartRepository.findByCustomerIdAndOrderIdIsNull(customerId)
            .stream().reduce((first, second) -> second);
        if (cartOp.isEmpty()) {
            return 0L;
        }
        var cart = cartOp.get();
        Long total = cartItemRepository.countItemInCart(cart.getId());
        if (total == null) {
            return 0L;
        }
        return total;
    }
}
