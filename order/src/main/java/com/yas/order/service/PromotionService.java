package com.yas.order.service;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.viewmodel.promotion.PromotionUsageVm;
import com.yas.order.viewmodel.promotion.PromotionVerifyResultDto;
import com.yas.order.viewmodel.promotion.PromotionVerifyVm;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;


@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionService extends AbstractCircuitBreakFallbackHandler {
    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleBodilessFallback")
    public void updateUsagePromotion(List<PromotionUsageVm> promotionUsageVms) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.promotion())
                .path("/backoffice/promotions/updateUsage")
                .buildAndExpand()
                .toUri();

        restClient.post()
                .uri(url)
                .headers(h -> h.setBearerAuth(jwt))
                .body(promotionUsageVms)
                .retrieve();
    }

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleVerifyFallback")
    public PromotionVerifyResultDto validateCouponCode(PromotionVerifyVm promotionVerifyVm) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getTokenValue();
        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.promotion())
            .path("/storefront/promotions/verify")
            .buildAndExpand()
            .toUri();

        PromotionVerifyResultDto promotionVerifyResultDto = restClient.post()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
            .body(promotionVerifyVm)
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                (request, response) -> {
                    throw new BadRequestException("Failed to apply promotion code: {}", promotionVerifyVm.couponCode());
                }
            )
            .body(PromotionVerifyResultDto.class);

        log.info("Promotion verify in service: {}", promotionVerifyResultDto);

        return promotionVerifyResultDto;
    }

    protected PromotionVerifyResultDto handleVerifyFallback(Throwable throwable) throws Throwable {
        return handleTypedFallback(throwable);
    }
}
