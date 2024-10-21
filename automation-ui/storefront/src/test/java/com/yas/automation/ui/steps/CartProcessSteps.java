package com.yas.automation.ui.steps;

import com.yas.automation.base.hook.WebDriverFactory;
import com.yas.automation.base.page.BasePage;
import com.yas.automation.ui.pages.CartPage;
import com.yas.automation.ui.pages.CategoryItemDetailPage;
import com.yas.automation.ui.pages.CategoryItemPage;
import com.yas.automation.ui.pages.CategoryPage;
import com.yas.automation.ui.services.AuthenticationService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.Duration;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CartProcessSteps extends BasePage {
    private final WebDriverFactory webDriverFactory;
    private final CategoryPage categoryPage;
    private final CategoryItemPage categoryItemPage;
    private final CategoryItemDetailPage categoryItemDetailPage;
    private final CartPage cartPage;
    private final AuthenticationService authenticationService;

    private String productName;

    public CartProcessSteps(WebDriverFactory webDriverFactory, CategoryPage categoryPage, CategoryItemPage categoryItemPage, CategoryItemDetailPage categoryItemDetailPage, CartPage cartPage, CartPage cartPage1, AuthenticationService authenticationService) {
        super(webDriverFactory);
        this.webDriverFactory = webDriverFactory;
        this.categoryPage = categoryPage;
        this.categoryItemPage = categoryItemPage;
        this.categoryItemDetailPage = categoryItemDetailPage;
        this.cartPage = cartPage1;
        this.authenticationService = authenticationService;
    }

    @Given("I login successful")
    public void i_login_successful() {
        authenticationService.loginSuccessful();
    }

    @When("I click on category item")
    public void i_click_on_category_item() {
        categoryPage.clickCategory();
    }

    @Then("I should be redirected to the product list")
    public void i_should_be_redirected_to_the_dashboard() {
        String currentUrl = webDriverFactory.getChromeDriver().getCurrentUrl();
        assertTrue(currentUrl.contains("/products"));
    }

    @When("I click on product item")
    public void i_click_on_product_item() throws InterruptedException {
        categoryItemPage.clickCategoryItem();
    }

    @Then("I should be redirected to the product item detail")
    public void i_should_be_redirected_to_the_product_item_detail() {
        if (categoryItemPage.hasProductList()) {
            boolean result = categoryItemDetailPage.existedButtonAddToCart();
            assertTrue(result);
        }
    }

    @When("I click on button add to cart")
    public void i_click_on_button_add_to_cart() throws InterruptedException {
        if (categoryItemPage.hasProductList()) {
            productName = categoryItemDetailPage.getProductName();
            categoryItemDetailPage.clickAddToCart();
        }
    }

    @When("I click on basket")
    public void i_click_on_basket() throws InterruptedException {
        if (categoryItemPage.hasProductList()) {
            categoryItemDetailPage.clickObBasket();
        }
    }

    @Then("This item is existed on table")
    public void this_item_is_existed_on_table() {
        if (categoryItemPage.hasProductList()) {
            String currentUrl = webDriverFactory.getChromeDriver().getCurrentUrl();
            assertTrue(currentUrl.contains("/cart"));
            assertTrue(cartPage.checkProductName(productName));
        }
    }

    @When("I click on icon delete on each row")
    public void iClickOnIconDeleteOnEachRow() {
        if (categoryItemPage.hasProductList()) {
            cartPage.clickDeleteButton();
        }
    }


    @Then("It shows popup confirm with button Remove")
    public void itShowsPopupConfirmWithButtonRemove() {
        if (categoryItemPage.hasProductList()) {
            productName = cartPage.getProductName();
            boolean result = cartPage.existedRemoveButton();
            assertTrue(result);
        }
    }

    @When("I click on button Remove")
    public void iClickOnButtonRemove() {
        if (categoryItemPage.hasProductList()) {
            cartPage.clickRemoveButton();
        }
    }

    @Then("The basket is empty or this cart item is not existed")
    public void theBasketIsEmptyOrThisCartItemIsNotExisted() {
        if (categoryItemPage.hasProductList()) {
            boolean result = cartPage.isBasketEmpty() || !cartPage.checkProductName(productName);
            assertTrue(result);
        }
    }

}
