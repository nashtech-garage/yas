package com.yas.automation.ui.pages;

import com.yas.automation.base.hook.WebDriverFactory;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.How;
import org.springframework.stereotype.Component;

import static com.yas.automation.base.util.WebElementUtil.getWebElementBy;

@Component
public class HomePage {

    private final WebDriverFactory webDriverFactory;

    public HomePage(WebDriverFactory webDriverFactory) {
        this.webDriverFactory = webDriverFactory;
    }

    /**
     * Click event to click to item on side menu
     * @param item it should correspond to value on fronted, usually entity name with (s)
     *             for example: products, brands, etc.
     */
    public void clickToCatalogItem(String item) {
        WebElement productLink = getWebElementBy(
                webDriverFactory.getChromeDriver(),
                How.XPATH,
                String.format("//li/a[@href='/catalog/%s']", item)
        );
        productLink.click();
    }

}
