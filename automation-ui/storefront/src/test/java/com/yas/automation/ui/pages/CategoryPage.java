package com.yas.automation.ui.pages;

import static com.yas.automation.base.util.WebElementUtil.getWebElementBy;

import com.yas.automation.base.hook.WebDriverFactory;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.How;
import org.springframework.stereotype.Component;

@Component
public class CategoryPage {
    private final WebDriverFactory webDriverFactory;

    public CategoryPage(WebDriverFactory webDriverFactory) {
        this.webDriverFactory = webDriverFactory;
    }

    public void clickCategory() {
        WebElement category = getWebElementBy(webDriverFactory.getChromeDriver(), How.CLASS_NAME, "image");
        category.click();
    }

}
