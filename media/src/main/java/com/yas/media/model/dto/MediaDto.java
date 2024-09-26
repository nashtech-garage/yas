package com.yas.media.model.dto;

import java.io.InputStream;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.MediaType;

@Builder
@Data
public class MediaDto {

    private InputStream content;
    private MediaType mediaType;
}