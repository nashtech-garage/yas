package com.yas.automation.ui.pages;

import com.yas.automation.base.hook.WebDriverFactory;
import com.yas.automation.base.util.WebElementUtil;
import static com.yas.automation.base.util.WebElementUtil.getWebElementBy;
import static com.yas.automation.base.util.WebElementUtil.waitElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.How;
import org.springframework.stereotype.Component;

@Component
public class CategoryItemPage {
    private final WebDriverFactory webDriverFactory;
    private boolean hasProductList;

    public CategoryItemPage(WebDriverFactory webDriverFactory) {
        this.webDriverFactory = webDriverFactory;
    }

    public void clickCategoryItem() {
        hasProductList = checkProductList();
        if (hasProductList) {
            WebElement categoryItem = getWebElementBy(webDriverFactory.getChromeDriver(), How.XPATH, "//div[contains(@class, 'ProductCard_product-card')]");
            categoryItem.click();
        }
    }

    private boolean checkProductList() {
        waitElement(webDriverFactory.getChromeDriver(), How.XPATH, "//div[@class='products-list']//a[starts-with(@href, '/products')]", 5);
        return WebElementUtil.isElementPresent(webDriverFactory.getChromeDriver(), How.XPATH, "//div[@class='products-list']//a[starts-with(@href, '/products')]");
    }

    public boolean hasProductList() {
        return hasProductList;
    }

}
