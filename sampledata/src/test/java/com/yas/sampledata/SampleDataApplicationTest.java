package com.yas.sampledata;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@ActiveProfiles("test")
class SampleDataApplicationTest {

    @Test
    void contextLoads() {
        // Just verify context loads
    }
}
