package com.yas.payment.service;

import static com.yas.commonlibrary.utils.AuthenticationUtils.extractJwt;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.yas.payment.config.ServiceUrlConfig;
import com.yas.payment.model.PaymentProvider;
import com.yas.payment.viewmodel.paymentprovider.MediaVm;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    public Map<Long, MediaVm> getMediaVmMap(List<PaymentProvider> providers) {
        if (providers.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            final URI url = getMediasUrl(providers);

            log.debug("Fetch media to get payment provider medias: {}", url);
            var medias = restClient.get()
                .uri(url)
                .headers(h -> h.setBearerAuth(extractJwt()))
                .retrieve()
                .body(new ParameterizedTypeReference<List<MediaVm>>() {});

            Map<Long, MediaVm> mediaVmMap = new HashMap<>();
            if (!isEmpty(medias)) {
                mediaVmMap = medias
                    .stream()
                    .collect(
                        toMap(
                            MediaVm::getId,
                            identity(),
                            (existing, duplicate) -> {
                                log.debug("Duplicate payment provider media {}", existing.getId());
                                return existing;
                            }
                        )
                    );
            }
            return mediaVmMap;
        } catch (Exception exception) {
            log.error("Get payment providers media got error: {}", exception.getMessage());
            return Collections.emptyMap();
        }
    }

    private URI getMediasUrl(List<PaymentProvider> providers) {
        var iconIds = providers.stream()
            .filter(pm -> Objects.nonNull(pm.getMediaId()))
            .map(pm -> String.valueOf(pm.getMediaId()))
            .collect(Collectors.joining(","));
        return UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.media())
            .path("/medias")
            .queryParam(IDS_PARAMS, iconIds)
            .build()
            .toUri();
    }

}
