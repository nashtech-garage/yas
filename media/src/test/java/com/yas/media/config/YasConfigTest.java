package com.yas.media.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "yas.publicUrl=http://localhost:8080"
})
class YasConfigTest {

    @Autowired
    private YasConfig yasConfig;

    @Test
    void testYasConfigCreation() {
        assertNotNull(yasConfig);
    }

    @Test
    void testYasConfigPublicUrl() {
        assertEquals("http://localhost:8080", yasConfig.publicUrl());
    }

    @Test
    void testYasConfigRecord() {
        YasConfig config = new YasConfig("http://example.com");
        assertEquals("http://example.com", config.publicUrl());
    }

    @Test
    void testYasConfigWithDifferentUrls() {
        YasConfig config1 = new YasConfig("http://localhost:8080");
        YasConfig config2 = new YasConfig("https://api.example.com");
        YasConfig config3 = new YasConfig("https://media.service.io");

        assertEquals("http://localhost:8080", config1.publicUrl());
        assertEquals("https://api.example.com", config2.publicUrl());
        assertEquals("https://media.service.io", config3.publicUrl());
    }

    @Test
    void testYasConfigEquality() {
        YasConfig config1 = new YasConfig("http://example.com");
        YasConfig config2 = new YasConfig("http://example.com");

        assertNotNull(config1);
        assertNotNull(config2);
        assertEquals(config1, config2);
    }

    @Test
    void testYasConfigPrefix() {
        assertNotNull(yasConfig);
        assertNotNull(yasConfig.publicUrl());
    }
}
