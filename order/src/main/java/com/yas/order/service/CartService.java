package com.yas.order.service;

import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.viewmodel.order.OrderItemVm;
import com.yas.order.viewmodel.order.OrderVm;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class CartService {

    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;

    public void deleteCartItem(OrderVm orderVm) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getTokenValue();
        List<Long> productIds = orderVm.orderItemVms()
                .stream()
                .map(OrderItemVm::productId)
                .toList();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.cart())
                .path("/storefront/cart-item/multi-delete")
                .queryParam("productIds", productIds)
                .buildAndExpand()
                .toUri();


        restClient.delete()
                .uri(url)
                .headers(h -> h.setBearerAuth(jwt))
                .retrieve();
    }
}
