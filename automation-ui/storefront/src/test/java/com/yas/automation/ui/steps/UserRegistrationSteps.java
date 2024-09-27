package com.yas.automation.ui.steps;

import static org.junit.Assert.assertTrue;

import com.yas.automation.ui.configuration.StorefrontConfiguration;
import com.yas.automation.ui.form.UserRegisterForm;
import com.yas.automation.ui.hook.WebDriverFactory;
import com.yas.automation.ui.pages.HomePage;
import com.yas.automation.ui.pages.UserRegistrationPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class UserRegistrationSteps {
    private final HomePage homePage;
    private final UserRegistrationPage userRegistrationPage;
    private final WebDriverFactory webDriverFactory;
    private final StorefrontConfiguration storefrontConf;
    private String INPUT_USERNAME;

    public UserRegistrationSteps(HomePage homePage, UserRegistrationPage userRegistrationPage, WebDriverFactory webDriverFactory, StorefrontConfiguration storefrontConf) {
        this.homePage = homePage;
        this.userRegistrationPage = userRegistrationPage;
        this.webDriverFactory = webDriverFactory;
        this.storefrontConf = storefrontConf;
    }

    @Given("I'm on the home page")
    public void iMOnTheHomePage() {
        webDriverFactory.getChromeDriver().navigate().to(storefrontConf.url());
    }

    @When("I click the login link on header")
    public void iClickTheLoginLinkOnHeader() {
        homePage.clickLogin();
    }

    @Then("I should be redirected to the welcome page")
    public void iShouldBeRedirectedToTheWelcomePage() {
        String currentUrl = webDriverFactory.getChromeDriver().getCurrentUrl();
        assertTrue(currentUrl.contains("/login"));
    }

    @When("I click on the register button")
    public void iClickOnTheRegisterButton() {
        userRegistrationPage.clickRegister();
    }

    @Then("I should be redirected to the register page")
    public void iShouldBeRedirectedToTheRegisterPage() {
        String currentUrl = webDriverFactory.getChromeDriver().getCurrentUrl();
        assertTrue(currentUrl.contains("/registration"));
    }

    @Given("I fill necessary data for registration user and click Register button")
    public void iFillNecessaryDataForRegistrationUserAndClickButton() {
        UserRegisterForm userRegisterForm = new UserRegisterForm(webDriverFactory.getChromeDriver());
        userRegistrationPage.fillUserRegistrationData(userRegisterForm);
        INPUT_USERNAME = userRegisterForm.getUsername().getAttribute("value");
        userRegisterForm.submitForm();

    }

    @Then("I should be redirected to the home page and display this user")
    public void iShouldBeRedirectedToTheHomePageAndDisplayThisUser() {
        String currentUrl = webDriverFactory.getChromeDriver().getCurrentUrl();
        assertTrue(currentUrl.contains(storefrontConf.url()));
        assertTrue(homePage.getUsername().contains(INPUT_USERNAME));
    }

    @Given("I fill invalid email and click Register button")
    public void iFillInvalidEmailAndClickRegisterButton() {
        UserRegisterForm userRegisterForm = new UserRegisterForm(webDriverFactory.getChromeDriver());
        userRegistrationPage.fillInvalidEmail(userRegisterForm);
        userRegisterForm.submitForm();
    }

    @Then("I should be kept the register page and display error message")
    public void iShouldBeKeptTheRegisterPageAndDisplayErrorMessage() {
        String currentUrl = webDriverFactory.getChromeDriver().getCurrentUrl();
        assertTrue(currentUrl.contains("/registration"));
        assertTrue(userRegistrationPage.existedErrorMessage());
    }

    @When("I click on Back to Login link")
    public void iClickOnBackToLoginLink() {
        userRegistrationPage.clickBackToLogin();
    }
}
