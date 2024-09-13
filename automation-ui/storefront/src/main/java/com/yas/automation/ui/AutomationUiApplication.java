package com.yas.automation.ui;

import com.yas.automation.ui.configuration.StorefrontConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorefrontConfiguration.class)
public class AutomationUiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutomationUiApplication.class, args);
	}

}
