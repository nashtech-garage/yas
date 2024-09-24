package com.yas.automation.ui.storefront.steps;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.yas.automation.ui.configuration.StorefrontConfiguration;
import com.yas.automation.ui.hook.WebDriverFactory;
import com.yas.automation.ui.storefront.pages.HomePage;
import com.yas.automation.ui.storefront.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;


public class LoginSteps {

    private final HomePage homePage;
    private final LoginPage loginPage;
    private final WebDriverFactory webDriverFactory;
    private final StorefrontConfiguration storefrontConf;

    @Autowired
    public LoginSteps(HomePage homePage, LoginPage loginPage, WebDriverFactory webDriverFactory,
                      StorefrontConfiguration storefrontConf) {
        this.homePage = homePage;
        this.loginPage = loginPage;
        this.storefrontConf = storefrontConf;
        this.webDriverFactory = webDriverFactory;
    }

    @Given("I am on the home page")
    public void i_am_on_the_home_page() {
        webDriverFactory.getChromeDriver().navigate().to(storefrontConf.url());
    }

    @When("I click on the login link")
    public void i_click_on_the_login_link() {
        homePage.clickLogin();
    }

    @Then("I should be redirected to the login page")
    public void i_should_be_redirected_to_the_login_page() {
        // Verify that the current URL is the login page
        String currentUrl = webDriverFactory.getChromeDriver().getCurrentUrl();
        assertTrue(currentUrl.contains("/login"));
    }

    @When("I enter valid credentials")
    public void i_enter_valid_credentials() {
        loginPage.login(storefrontConf.username(), storefrontConf.password());
    }

    @When("I click on the login button")
    public void i_click_on_the_login_button() {
        loginPage.clickLogin();
    }

    @Then("I should be redirected to the dashboard")
    public void i_should_be_redirected_to_the_dashboard() {

        WebDriverWait wait = new WebDriverWait(webDriverFactory.getChromeDriver(),
            Duration.of(10, ChronoUnit.SECONDS)); // 10 seconds wait
        WebElement userDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-dropdown")));
        String dropdownText = userDropdown.getText();

        // Assert to check if the correct text is present
        Assert.assertTrue("Check failed: User is not logged in as admin.",
            dropdownText.contains("Signed in as: admin"));
    }

    @When("I enter invalid credentials")
    public void i_enter_invalid_credentials() {
        String invalidUsername = "invalidAdmin";
        String invalidPassword = "invalidPassword";
        loginPage.login(invalidUsername, invalidPassword);
    }

    @Then("I should see an error message")
    public void i_should_see_an_error_message() {
        WebDriverWait wait = new WebDriverWait(webDriverFactory.getChromeDriver(),
            Duration.of(10, ChronoUnit.SECONDS)); // 10 seconds wait
        WebElement errorMessage = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert.alert-error .message-text")));

        // Assert the text content of the error message
        String expectedMessage = "Invalid username or password.";
        String actualMessage = errorMessage.getText();
        Assert.assertEquals("The error message is not as expected.", expectedMessage, actualMessage);

        System.out.println("Test passed: Correct error message is displayed.");
    }
}
