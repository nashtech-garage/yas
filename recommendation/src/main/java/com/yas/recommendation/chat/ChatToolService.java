package com.yas.recommendation.chat;

import static com.yas.recommendation.chat.constant.ChatConstant.AUTHORIZATION;

import com.yas.recommendation.chat.dto.AddToCartRequest;
import com.yas.recommendation.chat.util.ChatUtil;
import com.yas.recommendation.configuration.RecommendationConfig;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ChatToolService {

    public static final String BEARER_TEMPLATE = "Bearer %s";
    private final Logger logger = LoggerFactory.getLogger(ChatToolService.class);

    private final RestClient restClient;
    private final RecommendationConfig recommendationConfig;

    public ChatToolService(RestClient restClient, RecommendationConfig recommendationConfig) {
        this.restClient = restClient;
        this.recommendationConfig = recommendationConfig;
    }

    public boolean cancelOrder(long orderId) {
        try {
            final URI url = UriComponentsBuilder
                .fromHttpUrl(recommendationConfig.getOrderUrl())
                .path("/storefront/orders/{id}/cancel")
                .buildAndExpand(orderId)
                .toUri();
            restClient.put()
                .uri(url)
                .header(AUTHORIZATION, String.format(BEARER_TEMPLATE, ChatUtil.getUser().jwt()))
                .retrieve()
                .toBodilessEntity();
            return true;
        } catch (Exception exception) {
            logger.error("Cancel order {} got an issue {}", orderId, exception.getMessage());
            return false;
        }
    }

    public String getOrderStatus() {
        final URI url = UriComponentsBuilder
            .fromHttpUrl(recommendationConfig.getOrderUrl())
            .path("/storefront/orders/my-orders")
            .queryParam("productName", "")
            .buildAndExpand()
            .toUri();
        return restClient.get()
            .uri(url)
            .header(AUTHORIZATION, String.format(BEARER_TEMPLATE, ChatUtil.getUser().jwt()))
            .retrieve()
            .toEntity(new ParameterizedTypeReference<String>() {})
            .getBody();
    }

    public boolean addProductToCart(AddToCartRequest cart) {
        try {
            final URI url = UriComponentsBuilder
                .fromHttpUrl(recommendationConfig.getCartUrl())
                .path("/storefront/cart/items")
                .buildAndExpand()
                .toUri();
            restClient.post()
                .uri(url)
                .body(cart)
                .header(AUTHORIZATION, String.format(BEARER_TEMPLATE, ChatUtil.getUser().jwt()))
                .retrieve()
                .toBodilessEntity();
            return true;
        } catch (Exception exception) {
            logger.error("Add product {} to cart got an issue {}", cart.productId(), exception.getMessage());
            return false;
        }
    }

}
