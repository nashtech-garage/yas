package com.yas.search.service;

import com.yas.search.config.ServiceUrlConfig;
import com.yas.search.viewmodel.ProductESDetailVm;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.net.URI;

@Service
public class ProductESService {
    private final WebClient webClient;
    private final ServiceUrlConfig serviceUrlConfig;

    public ProductESService(WebClient webClient, ServiceUrlConfig serviceUrlConfig) {
        this.webClient = webClient;
        this.serviceUrlConfig = serviceUrlConfig;
    }

//    public ProductESDetailVm getProductESDetailById(Long productId) {
//        final URI url = UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.location()).path("/elasticsearch/products").queryParam("stateOrProvinceIds", stateOrProvinceIds).build().toUri();
//        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
//        return webClient.get()
//                .uri(url)
//                .headers(h -> h.setBearerAuth(jwt))
//                .retrieve()
//                .bodyToMono(new ParameterizedTypeReference<List<StateOrProvinceAndCountryGetNameVm>>() {
//                })
//                .block();
//    }

    public ProductESDetailVm getProductESDetailById(Long id) {
//        if(id == null){
//            //TODO return default no image url
//            return new NoFileMediaVm(null, "", "", "", "");
//        }
        final URI url = UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.product()).path("/storefront/products/{id}").buildAndExpand(id).toUri();
       // final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getTokenValue();
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(ProductESDetailVm.class)
                .block();
    }
}
