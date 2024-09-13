package com.yas.automation.ui.services;

import com.yas.automation.ui.configuration.StorefrontConfiguration;
import com.yas.automation.ui.hook.WebDriverFactory;
import com.yas.automation.ui.pages.HomePage;
import com.yas.automation.ui.pages.LoginPage;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertTrue;

@Component
public class AuthenticationService {
    private final HomePage homePage;
    private final LoginPage loginPage;
    private final WebDriverFactory webDriverFactory;
    private final StorefrontConfiguration storefrontConf;

    public AuthenticationService(HomePage homePage, LoginPage loginPage, WebDriverFactory webDriverFactory, StorefrontConfiguration storefrontConf) {
        this.homePage = homePage;
        this.loginPage = loginPage;
        this.webDriverFactory = webDriverFactory;
        this.storefrontConf = storefrontConf;
    }

    public void loginSuccessful() {
        webDriverFactory.getChromeDriver().navigate().to(storefrontConf.url());
        homePage.clickLogin();

        // Verify that the current URL is the login page
        String currentUrl = webDriverFactory.getChromeDriver().getCurrentUrl();
        assertTrue(currentUrl.contains("/login"));

        loginPage.login(storefrontConf.username(), storefrontConf.password());
        loginPage.clickLogin();

        WebDriverWait wait = new WebDriverWait(webDriverFactory.getChromeDriver(), Duration.of(15, ChronoUnit.SECONDS)); // 10 seconds wait
        WebElement userDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-dropdown")));
        //WebElement userDropdown = webDriver.findElement(By.id("user-dropdown"));
        String dropdownText = userDropdown.getText();

        // Assert to check if the correct text is present
        Assert.assertTrue("Check failed: User is not logged in as admin.", dropdownText.contains("Signed in as: admin"));
    }
}
