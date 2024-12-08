package com.yas.payment.service;

import static com.yas.commonlibrary.utils.AuthenticationUtils.extractJwt;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.yas.payment.config.ServiceUrlConfig;
import com.yas.payment.model.PaymentProvider;
import com.yas.payment.viewmodel.paymentprovider.MediaVm;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class MediaService extends AbstractCircuitBreakFallbackHandler {

    public static final String IDS_PARAMS = "ids";

    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;

    public MediaService(RestClient restClient, ServiceUrlConfig serviceUrlConfig) {
        this.restClient = restClient;
        this.serviceUrlConfig = serviceUrlConfig;
    }

    @Retry(name = "restApi")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "fallbackGetMediaVmMap")
    public Map<Long, MediaVm> getMediaVmMap(List<PaymentProvider> providers) {
        Set<Long> mediaIds = getMediaIds(providers);
        if (mediaIds.isEmpty()) {
            return Collections.emptyMap();
        }
        final URI url = getMediasUrl(mediaIds);
        log.debug("Fetch media to get payment provider medias: {}", url);
        var medias = restClient.get()
            .uri(url)
            .headers(h -> h.setBearerAuth(extractJwt()))
            .retrieve()
            .body(new ParameterizedTypeReference<List<MediaVm>>() {});

        return medias.stream().collect(toMap(MediaVm::getId, identity()));
    }

    private Set<Long> getMediaIds(List<PaymentProvider> providers) {
        return providers.stream()
                .map(PaymentProvider::getMediaId)
                .collect(Collectors.toSet());
    }

    private URI getMediasUrl(Set<Long> mediaIds) {
        var iconIds = mediaIds.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(","));
        return UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.media())
            .path("/medias")
            .queryParam(IDS_PARAMS, iconIds)
            .build()
            .toUri();
    }

    private Map<Long, MediaVm> fallbackGetMediaVmMap(List<PaymentProvider> providers,
            Throwable throwable) {
        log.error("Failed to get media for IDs: {}", providers.stream().map(PaymentProvider::getMediaId).toList());
        log.error("Fallback triggered for getMediaVmMap", throwable);
        return Collections.emptyMap();
    }

}