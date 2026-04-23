package com.yas.automation.ui.steps;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.yas.automation.ui.form.ProductForm;
import com.yas.automation.base.hook.WebDriverFactory;
import com.yas.automation.ui.pages.HomePage;
import com.yas.automation.ui.pages.ProductPage;
import com.yas.automation.ui.service.AuthenticationService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ProductProcessSteps {

    private String productName;

    private final HomePage homePage;
    private final ProductPage productPage;
    private final WebDriverFactory webDriverFactory;
    private final AuthenticationService authenticationService;

    public ProductProcessSteps(HomePage homePage, ProductPage productPage, WebDriverFactory webDriverFactory, AuthenticationService authenticationService) {
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

    @When("I click to edit icon on row")
    public void iClickToEditIconOnRow() {
        productPage.clickToEditProductBtn();
    }

    @Then("I should be in edit product page")
    public void iShouldBeInEditProductPage() {
        String currentUrl = webDriverFactory.getChromeDriver().getCurrentUrl();
        assertTrue(currentUrl.contains("/edit"));
    }

    @Given("I update necessary data for product and submit")
    public void iUpdateNecessaryDataForProductAndSubmit() {
        ProductForm productForm = new ProductForm(webDriverFactory.getChromeDriver());
        productPage.setNewProductName();
        productPage.scrollTo(productForm.getSaveBtn());
        productForm.saveForm();
        productName = productForm.getName().getAttribute("value");
    }

    @Then("Updated product shown in product list")
    public void updatedProductShownInProductList() {
        assertTrue(
                "Updated product must be shown on product list with new name.",
                productPage.isNewProductShow(this.productName)
        );
    }

    @When("I click to delete icon on row")
    public void iClickToDeleteIconOnRow() {
        productPage.clickToDeleteProductBtn();
    }

    @Then("It shows popup confirm with button Delete")
    public void itShowsPopupConfirmWithButtonDelete() {
        assertTrue(productPage.existedDeleteDialog());
    }

    @When("I click on button Delete")
    public void iClickOnButtonDelete() {
        productPage.clickToDeleteBtn();
    }

    @Then("This item is not existed in product list")
    public void thisItemIsNotExistedInProductList() {
        String productName = productPage.getProductName();
        assertFalse(productPage.isNewProductShow(productName));
    }
}
