package com.yas.automation.ui.pages;

import static com.yas.automation.ui.util.WebElementUtil.getWebElementBy;

import com.yas.automation.ui.hook.WebDriverFactory;
import com.yas.automation.ui.page.BasePage;
import com.yas.automation.ui.util.WebElementUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.How;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Component
public class CartPage extends BasePage {
    private final WebDriverFactory webDriverFactory;

    public CartPage(WebDriverFactory webDriverFactory) {
        super(webDriverFactory);
        this.webDriverFactory = webDriverFactory;
    }

    public boolean checkProductName(String productName) {
        this.wait(Duration.ofSeconds(1));

        // Get all rows (tr) inside the table body
        List<WebElement> rows = getAllRowsInBasket();

        // Loop through each row and get the product name
        for (WebElement row : rows) {
            // Get the product title in the current row
            WebElement productTitleElement = row.findElement(By.xpath(".//h6[@class='product-link']"));
            String productTitle = productTitleElement.getText();
            if (Objects.nonNull(productTitle) && productTitle.equals(productName)) {
                return true;
            }
        }
        return false;
    }

    // Locate all the rows in the basket table
    public List<WebElement> getAllRowsInBasket() {
        // Locate the table body containing the products
        WebElement tableBody = getWebElementBy(webDriverFactory.getChromeDriver(), How.XPATH, "//div[@class='shop__cart__table']//tbody");

        // Get all rows (tr) inside the table body
        return tableBody.findElements(By.tagName("tr"));
    }

    // Get the delete button for a specific row
    public void clickDeleteButton() {
        List<WebElement> rows = getAllRowsInBasket();
        // Locate the delete button in the row
        WebElement deleteButton = rows.get(0).findElement(By.className("remove_product"));
        deleteButton.click();  // Perform click action to delete the product
    }

    public boolean existedRemoveButton() {
        this.wait(Duration.ofSeconds(1));
        return WebElementUtil.isElementPresent(webDriverFactory.getChromeDriver(), How.XPATH, "//button[@type='button' and contains(text(),'Remove')]");
    }

    public void clickRemoveButton() {
        WebElement removeBtn = getWebElementBy(webDriverFactory.getChromeDriver(), How.XPATH, "//button[@type='button' and contains(text(),'Remove')]");
        removeBtn.click();
    }

    public String getProductName() {
        List<WebElement> rows = getAllRowsInBasket();
        // Locate the delete button in the row
        WebElement product = rows.get(0).findElement(By.className("product-link"));
        return product.getText();
    }
}

