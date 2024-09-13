package com.yas.automation.ui.steps;

import static org.junit.Assert.assertTrue;

import com.yas.automation.ui.hook.WebDriverFactory;
import com.yas.automation.ui.pages.CategoryPage;
import com.yas.automation.ui.pages.HomePage;
import com.yas.automation.ui.pages.NewCategoryPage;
import com.yas.automation.ui.service.AuthenticationService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class CreateCategorySteps {
    private final AuthenticationService authenticationService;
    private final WebDriverFactory webDriverFactory;
    private final HomePage homePage;
    private final CategoryPage categoryPage;
    private final NewCategoryPage newCategoryPage;

    public CreateCategorySteps(AuthenticationService authenticationService, WebDriverFactory webDriverFactory,
                               HomePage homePage, CategoryPage categoryPage, NewCategoryPage newCategoryPage) {
        this.authenticationService = authenticationService;
        this.webDriverFactory = webDriverFactory;
        this.homePage = homePage;
        this.categoryPage = categoryPage;
        this.newCategoryPage = newCategoryPage;
    }

    @Given("I am logged in successfully")
    public void i_am_logged_in_successfully() {
        authenticationService.authorizeWithAdminUser();
    }

    @When("I click on the {string} option in the menu")
    public void i_click_on_option_in_the_menu(String option) {
        homePage.clickToCatalogItem(option);
    }

    @Then("I should be redirected to the category list page")
    public void i_should_be_redirected_to_category_list_page() {
        String currentUrl = webDriverFactory.getChromeDriver().getCurrentUrl();
        assertTrue(currentUrl.contains("/catalog/categories"));
    }

    @When("I click on the Create Category button")
    public void i_click_on_button() throws InterruptedException {
        Thread.sleep(1000);
        categoryPage.clickCreateCategoryButton();
    }

    @Then("I should be redirected to the create category page")
    public void i_should_be_redirected_to_create_category_page() {
        String currentUrl = webDriverFactory.getChromeDriver().getCurrentUrl();
        assertTrue(currentUrl.contains("/catalog/categories/create"));
    }

    @Given("I have filled in all the necessary data for the new category")
    public void i_have_filled_in_all_the_necessary_data_for_new_category() {
        newCategoryPage.fillInAllNecessaryField();
    }

    @When("I click the Save button")
    public void iClickTheButton() throws InterruptedException {
        newCategoryPage.clickSaveButton();
        Thread.sleep(2000);
    }

    @Then("I should be redirected to the category list page again")
    public void i_should_be_redirected_to_category_list_page_again() {
        String currentUrl = webDriverFactory.getChromeDriver().getCurrentUrl();
        assertTrue(currentUrl.contains("/catalog/categories"));
    }

    @Then("the new category should be displayed in the category list")
    public void the_new_category_should_be_displayed_in_the_category_list() {
        WebElement row = webDriverFactory.getChromeDriver().findElement(By.xpath("//tr[td/text()='" + newCategoryPage.getCreatedName() + "']"));

        // Verify that the row with the given category name is displayed
        assertTrue("The category " + newCategoryPage.getCreatedName() + " is not displayed in the list.", row.isDisplayed());
    }
}

