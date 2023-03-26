package com.yas.search.service;

import com.yas.search.config.ServiceUrlConfig;
import com.yas.search.viewmodel.NoFileMediaVm;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class MediaService {
    private final WebClient webClient;
    private final ServiceUrlConfig serviceUrlConfig;

    public MediaService(WebClient webClient, ServiceUrlConfig serviceUrlConfig) {
        this.webClient = webClient;
        this.serviceUrlConfig = serviceUrlConfig;
    }

    public NoFileMediaVm getMedia(Long id){
        if(id == null){
            //TODO return default no image url
            return new NoFileMediaVm(null, "", "", "", "");
        }
        final URI url = UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.media()).path("/medias/{id}").buildAndExpand(id).toUri();
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(NoFileMediaVm.class)
                .block();
    }
}
