package com.yas.automation.ui.hook;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Cucumber hook managing cucumber test scenarios
 */
public class Hooks {
    @Autowired
    private WebDriverFactory webDriverFactory;

    @Before
    public void setUp() {
        webDriverFactory.getChromeDriver();
    }

    @After
    public void tearDown() {
        webDriverFactory.getChromeDriver().close();
        webDriverFactory.destroyDriver();
    }
}
