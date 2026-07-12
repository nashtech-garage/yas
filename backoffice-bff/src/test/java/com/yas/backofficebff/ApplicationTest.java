package com.yas.backofficebff;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

class ApplicationTest {

    @Test
    void main_shouldInvokeSpringApplicationRun() {
        String[] args = new String[] {"--spring.profiles.active=test"};

        try (MockedStatic<SpringApplication> springApplication = Mockito.mockStatic(SpringApplication.class)) {
            Application.main(args);

            springApplication.verify(() -> SpringApplication.run(eq(Application.class), eq(args)), times(1));
        }
    }
}
