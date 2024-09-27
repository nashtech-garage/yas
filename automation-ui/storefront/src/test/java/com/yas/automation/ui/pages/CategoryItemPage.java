package com.yas.automation.ui.pages;

import static com.yas.automation.ui.util.WebElementUtil.getWebElementBy;

import com.yas.automation.ui.hook.WebDriverFactory;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.How;
import org.springframework.stereotype.Component;

@Component
public class CategoryItemPage {
    private final WebDriverFactory webDriverFactory;

    public CategoryItemPage(WebDriverFactory webDriverFactory) {
        this.webDriverFactory = webDriverFactory;
    }

    public void clickCategoryItem() {
        WebElement categoryItem = getWebElementBy(webDriverFactory.getChromeDriver(), How.XPATH, "//div[contains(@class, 'ProductCard_product-card')]");
        categoryItem.click();
    }
}
