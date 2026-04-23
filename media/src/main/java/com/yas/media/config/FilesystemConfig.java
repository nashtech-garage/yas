package com.yas.media.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class FilesystemConfig {

    @Value("${file.directory}")
    private String directory;

}
