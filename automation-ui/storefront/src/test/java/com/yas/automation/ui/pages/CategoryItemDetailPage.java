package com.yas.automation.ui.pages;

import static com.yas.automation.base.util.WebElementUtil.getWebElementBy;

import com.yas.automation.base.hook.WebDriverFactory;
import com.yas.automation.base.page.BasePage;
import com.yas.automation.base.util.WebElementUtil;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.How;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CategoryItemDetailPage extends BasePage {
    private final WebDriverFactory webDriverFactory;

    public CategoryItemDetailPage(WebDriverFactory webDriverFactory) {
        super(webDriverFactory);
        this.webDriverFactory = webDriverFactory;
    }

    public boolean existedButtonAddToCart() {
        this.wait(Duration.ofSeconds(1));
        return WebElementUtil.isElementPresent(webDriverFactory.getChromeDriver(), How.XPATH, "//button[span[text()='Add to cart']]");
    }

    public void clickAddToCart() {
        WebElement btnAddToCart = getWebElementBy(webDriverFactory.getChromeDriver(), How.XPATH, "//button[span[text()='Add to cart']]");
        btnAddToCart.click();
    }

    public void clickObBasket() {
        this.wait(Duration.ofSeconds(5));
        WebElement btnBasket = getWebElementBy(webDriverFactory.getChromeDriver(), How.CLASS_NAME, "header-cart");
        btnBasket.click();
    }

    public String getProductName() {
        WebElement title = getWebElementBy(webDriverFactory.getChromeDriver(), How.CLASS_NAME, "title");
        return title.getText();
    }

}
