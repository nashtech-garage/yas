package com.yas.automation.ui;

import com.yas.automation.ui.configuration.BackOfficeConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.yas.automation")
@EnableConfigurationProperties(BackOfficeConfiguration.class)
public class AutomationUiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutomationUiApplication.class, args);
	}

}
