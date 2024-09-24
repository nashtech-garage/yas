package com.yas.automation.ui;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/storefront/features",
    glue = "com.yas.automation.ui",
    plugin = {"pretty", "html:target/cucumber-reports"}
)
public class JUnitCucumberRunner {
}
