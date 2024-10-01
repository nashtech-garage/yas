package com.yas.automation.ui.steps;

import com.yas.automation.ui.configuration.BackOfficeConfiguration;
import com.yas.automation.base.hook.WebDriverFactory;
import com.yas.automation.ui.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class LoginSteps {
    private final LoginPage loginPage;
    private final WebDriverFactory webDriverFactory;
    private final BackOfficeConfiguration backOfficeConf;

    @Autowired
    public LoginSteps(LoginPage loginPage, WebDriverFactory webDriverFactory, BackOfficeConfiguration backOfficeConf) {
        this.loginPage = loginPage;
        this.backOfficeConf = backOfficeConf;
        this.webDriverFactory = webDriverFactory;
    }

    @Given("I am on the home page")
    public void i_am_on_the_home_page() {
        webDriverFactory.getChromeDriver().navigate().to(backOfficeConf.url());
    }

    @When("I enter valid credentials")
    public void i_enter_valid_credentials() {
        loginPage.login(backOfficeConf.username(), backOfficeConf.password());
    }

    @When("I click on the login button")
    public void i_click_on_the_login_button() {
        loginPage.clickLogin();
    }

    @Then("I should be redirected to the dashboard")
    public void i_should_be_redirected_to_the_dashboard() {
        WebDriverWait wait = new WebDriverWait(webDriverFactory.getChromeDriver(), Duration.of(10, ChronoUnit.SECONDS));
        WebElement userDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("navbar-text")));
        String dropdownText = userDropdown.getText();
        Assert.assertTrue("Check failed: User is not logged in as admin.", dropdownText.contains("Signed in as: admin"));
    }

    @When("I enter invalid credentials")
    public void i_enter_invalid_credentials() {
        String invalidUsername = "invalidAdmin";
        String invalidPassword = "invalidPassword";
        loginPage.login(invalidUsername, invalidPassword);
    }

    @Then("I should see an error message")
    public void i_should_see_an_error_message() {
        WebDriverWait wait = new WebDriverWait(webDriverFactory.getChromeDriver(), Duration.of(10, ChronoUnit.SECONDS)); // 10 seconds wait
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert.alert-error .message-text")));

        // Assert the text content of the error message
        String expectedMessage = "Invalid username or password.";
        String actualMessage = errorMessage.getText();
        Assert.assertEquals("The error message is not as expected.", expectedMessage, actualMessage);

        System.out.println("Test passed: Correct error message is displayed.");
    }
}
