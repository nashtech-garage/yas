package com.yas.media;

import com.yas.commonlibrary.config.CorsConfig;
import com.yas.media.config.YasConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"com.yas.media", "com.yas.commonlibrary"})
@EnableConfigurationProperties({YasConfig.class, CorsConfig.class})
public class MediaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MediaApplication.class, args);
    }

}
