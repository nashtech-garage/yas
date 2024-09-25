package com.yas.automation.ui.steps;

import com.yas.automation.ui.form.ProductForm;
import com.yas.automation.ui.hook.WebDriverFactory;
import com.yas.automation.ui.pages.HomePage;
import com.yas.automation.ui.pages.ProductPage;
import com.yas.automation.ui.service.AuthenticationService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertTrue;

public class CreateProductSteps {

    private String productName;

    private final HomePage homePage;
    private final ProductPage productPage;
    private final WebDriverFactory webDriverFactory;
    private final AuthenticationService authenticationService;

    public CreateProductSteps(HomePage homePage, ProductPage productPage, WebDriverFactory webDriverFactory, AuthenticationService authenticationService) {
        this.homePage = homePage;
        this.productPage = productPage;
        this.webDriverFactory = webDriverFactory;
        this.authenticationService = authenticationService;
    }

    @Given("I logged in successfully")
    public void i_should_be_logged_in_successfully() {
        authenticationService.authorizeWithAdminUser();
    }

    @When("I click to product on menu")
    public void i_should_click_to_product_on_menu() {
        homePage.clickToCatalogItem("products");
    }

    @Then("I should be in product list page")
    public void i_should_be_in_product_list_page() {
        String currentUrl = webDriverFactory.getChromeDriver().getCurrentUrl();
        assertTrue(currentUrl.contains("/catalog/products"));
    }

    @When("I click to create product button")
    public void i_should_click_to_create_product_button() {
        productPage.clickToCreateProductBtn();
    }

    @Then("I should be in create product page")
    public void i_should_be_in_create_product_page() {
        String currentUrl = webDriverFactory.getChromeDriver().getCurrentUrl();
        assertTrue(currentUrl.contains("/catalog/products/create"));
    }

    @Given("I fill necessary data for product and submit")
    public void i_should_fill_necessary_data_for_product() {
        ProductForm productForm = new ProductForm(webDriverFactory.getChromeDriver());

        // create product data
        productPage.fillGeneralProductData(productForm);
        productPage.uploadProductImg(productForm);
        productPage.fillProductVariants(productForm);
        productPage.fillProductAttribute(productForm);
        productPage.fillCategoryMapping(productForm);
        productPage.fillRelatedProduct(productForm);
        productPage.fillCrossSellProduct(productForm);
        productPage.fillSEOProduct(productForm);

        // submit form
        productPage.scrollTo(productForm.getSubmitBtn());
        productForm.submitForm();
        productName = productForm.getName().getAttribute("value");
    }

    @Then("Created product shown in product list")
    public void createdProductShownInProductList() throws Exception {
        assertTrue(
                "New product must be shown on product list",
                productPage.isNewProductShow(this.productName)
        );
    }
}
