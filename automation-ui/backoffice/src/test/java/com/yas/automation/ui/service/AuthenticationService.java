package com.yas.automation.ui.service;

import com.yas.automation.ui.configuration.BackOfficeConfiguration;
import com.yas.automation.base.hook.WebDriverFactory;
import com.yas.automation.ui.pages.LoginPage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthenticationService {

    private final LoginPage loginPage;
    private final WebDriverFactory webDriverFactory;
    private final BackOfficeConfiguration backOfficeConf;

    public void authorizeWithAdminUser() {
        webDriverFactory.getChromeDriver().navigate().to(backOfficeConf.url());
        loginPage.login(backOfficeConf.username(), backOfficeConf.password());
        loginPage.clickLogin();
    }

}
