package com.yas.recommendation.chat;

import com.yas.recommendation.chat.dto.AddToCartRequest;
import com.yas.recommendation.chat.dto.CancelOrderRequest;
import java.util.List;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

@Configuration
public class Tools {

    private final Logger logger = LoggerFactory.getLogger(Tools.class);

    private final ChatToolService chatToolService;

    public Tools(ChatToolService chatToolService) {
        this.chatToolService = chatToolService;
    }

    public void createOrder() {
        // 1.
        // confirm checkout items
        // create checkout

        // 2.
        // get address
        // create order
    }

    @Bean
    @Description("Cancel Order")
    public Function<CancelOrderRequest, String> cancelOrder() {
        return request -> {
            logger.info("Assistant process cancelling order: {}", request.orderId());
            boolean isSuccess = chatToolService.cancelOrder(request.orderId());
            return isSuccess ? "Cancel order successfully" : "Cancel order fail";
        };
    }

    @Bean
    @Description("Get order status")
    public Function<OrderStatusRequest, String> getOrderStatus() {
        return request -> {
            logger.debug("Assistant process checking order status");
            return chatToolService.getOrderStatus();
        };
    }

    @Bean
    @Description("Add Product to cart")
    public Function<AddToCartRequest, String> addToCart() {
        return request -> {
            logger.info("Assistant process adding product to cart: {}", request.productId());
            boolean isSuccess = chatToolService.addProductToCart(request);
            return isSuccess ? "Add to card successfully" : "Add to card fail";
        };
    }

    @Bean
    @Description("Search Product")
    public Function<SearchRequest, List<Product>> searchProduct() {
        return request -> {
            System.out.println("Search for product " + request);
            return getContent();
        };
    }

    @Bean
    @Description("ES search product")
    public Function<SearchRequest, List<Product>> esSearchProduct() {
        return request -> {
            System.out.println("ES search for product " + request);
            return getContent();
        };
    }

    private List<Product> getContent() {
        return List.of(
            new Product(
                "Dell Inspiron 16 5640 -  Intel Core Ultra processors deliver the next level in an immersive graphics experience, and high-performance low power processing\n" +
                    "Product Id is 16L"
            ),
            new Product(
                "Laptop Dell XPS 14 9440 - ProductId is 5L -  Unlock non-stop creativity with the new Dell XPS 14, a perfect balance of go-anywhere mobility and high performance" +
                    "ProductId is 5L"
            ),
            new Product("MacBook Pro 14 M3 Pro - ProductId is 12L -  The 14-inch MacBook Pro blasts forward with M3 Pro and M3 Max, radically advanced chips that drive even greater performance for more demanding workflows. - " +
                "ProductId is 12L"
            ),
            new Product("MacBook Air M2 - ProductId is 13L -  Supercharged by the next-generation M2 chip, the redesigned MacBook Air - " +
                "ProductId is 13L")
        );
    }

}

record Product(String content) {}
record SearchRequest(String prompt) {}
record OrderStatusRequest(String userId) {}
