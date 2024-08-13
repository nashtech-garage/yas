package com.yas.customer.service;


import com.yas.customer.config.ServiceUrlConfig;
import com.yas.customer.viewmodel.address.AddressDetailVm;
import com.yas.customer.viewmodel.address.AddressPostVm;
import com.yas.customer.viewmodel.address.AddressVm;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;

    public List<AddressDetailVm> getAddressesByIdList(List<Long> ids) {
        final String jwt =
            ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.location())
            .path("/storefront/addresses")
            .queryParam("ids", ids)
            .buildAndExpand()
            .toUri();

        return restClient.get()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
            .retrieve()
            .body(new ParameterizedTypeReference<List<AddressDetailVm>>() {
            });
    }

    public AddressDetailVm getAddressById(Long id) {
        final String jwt =
            ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.location())
            .path("/storefront/addresses/{id}")
            .buildAndExpand(id)
            .toUri();

        return restClient.get()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
            .retrieve()
            .body(AddressDetailVm.class);
    }

    public AddressVm createAddress(AddressPostVm addressPostVm) {
        final String jwt =
            ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        final URI url = UriComponentsBuilder
            .fromHttpUrl(serviceUrlConfig.location())
            .path("/storefront/addresses")
            .buildAndExpand()
            .toUri();

        return restClient.post()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
            .body(addressPostVm)
            .retrieve()
            .body(AddressVm.class);
    }
}
