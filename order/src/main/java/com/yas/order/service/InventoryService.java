package com.yas.order.service;

import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.exception.QuantityOutOfStockException;
import com.yas.order.viewmodel.ErrorVm;
import com.yas.order.viewmodel.StockRequest;
import com.yas.order.viewmodel.StockVM;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final WebClient webClient;
    private final ServiceUrlConfig serviceUrlConfig;

    public List<StockVM> checkStockAvailable(List<StockRequest> stockRequests) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.inventory())
                .path("/backoffice/stocks/check-stock-available")
                .buildAndExpand()
                .toUri();


        return webClient.put()
                .uri(url)
                .headers(h->h.setBearerAuth(jwt))
                .bodyValue(stockRequests)
                .retrieve().onStatus(HttpStatusCode::isError,
                        response -> response.bodyToMono(ErrorVm.class)
                                .flatMap(errorVm ->
                                        Mono.error(new QuantityOutOfStockException(errorVm.detail()))))
                .bodyToMono(new ParameterizedTypeReference<List<StockVM>>() {}).block();
    }
}
