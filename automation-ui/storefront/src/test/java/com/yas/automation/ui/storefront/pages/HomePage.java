package com.yas.automation.ui.storefront.pages;

import com.yas.automation.ui.hook.WebDriverFactory;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.How;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.yas.automation.ui.ultil.WebElementUtil.getWebElementBy;

@Component
public class HomePage {

    @Autowired
    private final WebDriverFactory webDriverFactory;

    @Autowired
    public HomePage(WebDriverFactory webDriverFactory) {
        this.webDriverFactory = webDriverFactory;
    }

    public void clickLogin() {
        WebElement loginBtn = getWebElementBy(webDriverFactory.getChromeDriver(), How.LINK_TEXT, "Login");
        loginBtn.click();
    }

}
