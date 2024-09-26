package com.yas.media.model.dto;

import java.io.InputStream;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.MediaType;

@Builder
@Getter
public class MediaDto {

    private InputStream content;
    private MediaType mediaType;
}