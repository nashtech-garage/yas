package com.yas.product.service;

import com.yas.product.viewmodel.NoFileMediaVm;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class MediaService {
    private final WebClient webClient;

    public MediaService(WebClient webClient) {
        this.webClient = webClient;
    }

    public NoFileMediaVm SaveFile(MultipartFile multipartFile, String caption, String fileNameOverride){
        final URI url = UriComponentsBuilder.fromHttpUrl("http://api.yas.local/media/medias").build().toUri();

        final MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("multipartFile", multipartFile.getResource());
        builder.part("caption", caption);
        builder.part("fileNameOverride", fileNameOverride);

        NoFileMediaVm noFileMediaVm = webClient.post()
                .uri(url)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(NoFileMediaVm.class)
                .block();
        return noFileMediaVm;
    }
}
