package com.yas.automation.ui.pages;

import com.yas.automation.ui.enumerate.ProductAttribute;
import com.yas.automation.ui.form.InputType;
import com.yas.automation.ui.form.ProductForm;
import com.yas.automation.ui.hook.WebDriverFactory;
import com.yas.automation.ui.page.BasePage;
import com.yas.automation.ui.service.InputDelegateService;
import com.yas.automation.ui.util.WebElementUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.How;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.yas.automation.ui.enumerate.ProductAttribute.BASE_XPATH;
import static com.yas.automation.ui.util.WebElementUtil.getWebElementBy;
import static org.junit.Assert.assertTrue;

@Component
public class ProductPage extends BasePage {

    public static final int NUM_OF_TABLE_ROW = 6;
    public static final int NUM_OF_DEFAULT_ATTRIBUTE = 7;
    public static final String SAMPLE_TEMPLATE = "Sample Template";
    public static final String DUMP_FILE_PATH = "sampledata/images/dell.jpg";

    private final WebDriverFactory webDriverFactory;
    private final InputDelegateService inputDelegateService;

    public ProductPage(WebDriverFactory webDriverFactory, InputDelegateService inputDelegateService) {
        super(webDriverFactory);
        this.webDriverFactory = webDriverFactory;
        this.inputDelegateService = inputDelegateService;
    }

    public void clickToCreateProductBtn() {
        WebElement createProductLink = getWebElementBy(
                webDriverFactory.getChromeDriver(),
                How.CSS,
                "a[href='/catalog/products/create']"
        );
        createProductLink.click();
    }

    public void fillGeneralProductData(ProductForm productForm) {
        // General Information
        inputDelegateService.setInputValue(InputType.TEXT, productForm.getName(), String.format("Dell-%s", UUID.randomUUID()));
        inputDelegateService.setInputValue(InputType.TEXT, productForm.getPrice(), "100000000");
        inputDelegateService.setInputValue(InputType.TEXT, productForm.getSku(), UUID.randomUUID().toString());
        inputDelegateService.setInputValue(InputType.TEXT, productForm.getGtin(), UUID.randomUUID().toString());
        inputDelegateService.setInputValue(InputType.TEXT, productForm.getDescription(), UUID.randomUUID().toString());
        inputDelegateService.setInputValue(InputType.TEXT, productForm.getShortDescription(), UUID.randomUUID().toString());
        scrollTo(productForm.getIsFeatured());
        inputDelegateService.setInputValue(InputType.CHECKBOX, productForm.getIsFeatured(), null);
        inputDelegateService.setInputValue(InputType.DROPDOWN, productForm.getBrand(), "Apple");
        inputDelegateService.setInputValue(InputType.DROPDOWN, productForm.getTax(), "Value Added Tax (VAT)");
    }

    public void uploadProductImg(ProductForm productForm) {
        scrollTo(productForm.getProductImgNav());
        this.wait(Duration.ofSeconds(2));
        productForm.getProductImgNav().click();

        inputDelegateService.setInputValue(InputType.FILE, productForm.getImage(), DUMP_FILE_PATH);
        boolean isProductImgPreviewVisible = isElementPresent(
                () -> WebElementUtil.getWebElementBy(
                        webDriverFactory.getChromeDriver(),
                        How.XPATH,
                        "//img[@alt='Image']")
        );
        assertTrue("Product Image preview must be visible", isProductImgPreviewVisible);

        inputDelegateService.setInputValue(InputType.FILE, productForm.getThumbnail(), DUMP_FILE_PATH);
        boolean isThumbnailPreviewVisible = isElementPresent(
                () -> WebElementUtil.getWebElementBy(
                        webDriverFactory.getChromeDriver(),
                        How.XPATH,
                        "//img[@alt='Thumbnail']")
        );
        assertTrue("Thumbnail preview must be visible", isThumbnailPreviewVisible);
    }

    public void fillProductVariants(ProductForm productForm) {
        scrollTo(productForm.getProductVariantsNav());
        productForm.getProductVariantsNav().click();

        // Select available options
        inputDelegateService.setInputValue(InputType.TEXT, productForm.getProductVariationAvailableOption(), "RAM");
        productForm.getProductVariationAvailableOptionListBox().click();

        // Add Option
        productForm.getAddOptionBtn().click();
        inputDelegateService.setInputValue(InputType.TEXT, productForm.getRam(), "32GB");

        // Add combination
        productForm.getGenerateCombinationBtn().click();
        inputDelegateService.setInputValue(InputType.TEXT, productForm.getRamOptionPrice(), "2000000");
        inputDelegateService.setInputValue(InputType.TEXT, productForm.getRamOptionGtin(), UUID.randomUUID().toString());
        inputDelegateService.setInputValue(InputType.TEXT, productForm.getRamOptionSku(), UUID.randomUUID().toString());

        // Add combination images
        inputDelegateService.setInputValue(InputType.FILE, productForm.getRamThumbnail(), DUMP_FILE_PATH);
        inputDelegateService.setInputValue(InputType.FILE, productForm.getProductSubImage(), DUMP_FILE_PATH);
    }

    public void fillProductAttribute(ProductForm productForm) {
        scrollTo(productForm.getProductAttributeNav());
        productForm.getProductAttributeNav().click();

        inputDelegateService.setInputValue(InputType.DROPDOWN, productForm.getProductTemplate(), SAMPLE_TEMPLATE);
        inputDelegateService.setInputValue(
                InputType.DROPDOWN,
                productForm.getProductTemplateAvailableAttribute(),
                ProductAttribute.CPU.toString()
        );
        productForm.getProductTemplateApplyButton().click();

        // Add product attributes
        for (int i = 1; i < NUM_OF_DEFAULT_ATTRIBUTE; i++) {
            WebElementUtil.getWebElementBy(
                    webDriverFactory.getChromeDriver(),
                            How.XPATH,
                            BASE_XPATH.formatted(i)
                    ).sendKeys(UUID.randomUUID().toString());
        }
        scrollTo(productForm.getProductAttributeCreateBtn());
    }

    public void fillCategoryMapping(ProductForm productForm) {
        scrollTo(productForm.getCategoryMappingNav());
        productForm.getCategoryMappingNav().click();
        inputDelegateService.setInputValue(InputType.CHECKBOX, productForm.getCatMappingLaptopChkBox(), null);
    }

    public void fillRelatedProduct(ProductForm productForm) {
        scrollTo(productForm.getRelatedProductNav());
        productForm.getRelatedProductNav().click();
        productForm.getRelatedProductAddBtn().click();
        getFirstCheckBoxElementInModalList("//*/table/tbody/tr[1]/td[1]").click();
        productForm.getModalCloseBtn().click();
    }

    private WebElement getFirstCheckBoxElementInModalList(String identity) {
        return WebElementUtil.getWebElementBy(
                webDriverFactory.getChromeDriver(),
                How.XPATH,
                identity

        ).findElement(By.cssSelector("input[type='checkbox'].form-check-input"));
    }

    public void fillCrossSellProduct(ProductForm productForm) {
        scrollTo(productForm.getCrossSellProductNav());
        productForm.getCrossSellProductNav().click();
        productForm.getManagedCrossSellProductBtn().click();
        getFirstCheckBoxElementInModalList("/html/body/div[3]/div/div/div[2]/table/tbody/tr[1]/td[1]").click();
        productForm.getModalCloseBtn().click();
    }

    public void fillSEOProduct(ProductForm productForm) {
        scrollTo(productForm.getSeoNav());
        productForm.getSeoNav().click();
        inputDelegateService.setInputValue(InputType.TEXT, productForm.getSeoMetaTitle(), UUID.randomUUID().toString());
        inputDelegateService.setInputValue(InputType.TEXT, productForm.getSeoMetaKeyword(), UUID.randomUUID().toString());
        inputDelegateService.setInputValue(InputType.TEXT, productForm.getSeoMetaDescription(), UUID.randomUUID().toString());
    }

    /**
     * This method used to look up all pages until we find the created product.
     * Currently, It does not show at first page, as sample data lack of lastModified field
     * (product list is order by lastModified desc then sample data always on top pages)
     * @param newProductName productName which we look for
     * @return is the new product show in all pages or not.
     */
    public boolean isNewProductShow(String newProductName) {
        final WebDriver chromeDriver = webDriverFactory.getChromeDriver();
        WebElement paginationBar = getWebElementBy(
                chromeDriver,
                How.CLASS_NAME,
                "pagination-container"
        );
        List<WebElement> pageItems = paginationBar.findElements(By.tagName("li"));

        // pageItems.size() - 2 >> is to skip "Next" link
        /*
         * for example: 1 2 3 ... 12 Next
         * >> try to get '12'
         */
        int totalPage = pageItems.isEmpty() ? 0 : Integer.parseInt(pageItems.get(pageItems.size() - 2).getText());
        final String baseProductItemXPath = "//*/table/tbody/tr[%s]/td[2]";
        int currentPage = 1;
        while (currentPage < totalPage) {
            // check all rows in table
            for (int i = 1; i < NUM_OF_TABLE_ROW; i++) {
                WebElement productName = getWebElementBy(
                        chromeDriver,
                        How.XPATH,
                        baseProductItemXPath.formatted(i)
                );
                if (Objects.equals(newProductName, productName.getText())) {
                    return true;
                }
            }
            currentPage++;
            getWebElementBy(chromeDriver, How.LINK_TEXT, String.valueOf(currentPage)).click(); // next page
        }
        return false;
    }

}
