package com.yas.media.model.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.MediaType;

@Builder
@Getter
public class MediaDto {

    private byte[] content;
    private MediaType mediaType;
}