package com.yas.automation.ui.storefront.pages;

import com.yas.automation.ui.hook.WebDriverFactory;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.How;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.yas.automation.ui.ultil.WebElementUtil.getWebElementBy;

@Component
public class LoginPage {

    @Autowired
    private final WebDriverFactory webDriverFactory;

    public void login(String username, String password) {
        WebElement usernameEle = getWebElementBy(webDriverFactory.getChromeDriver(), How.ID, "username");
        usernameEle.sendKeys(username);

        WebElement passwordEle = getWebElementBy(webDriverFactory.getChromeDriver(), How.ID, "password");
        passwordEle.sendKeys(password);
    }

    @Autowired
    public LoginPage(WebDriverFactory webDriverFactory) {
        this.webDriverFactory = webDriverFactory;
    }

    public void clickLogin() {
        WebElement loginBtn = getWebElementBy(webDriverFactory.getChromeDriver(), How.CLASS_NAME, "submit");
        loginBtn.click();
    }
}
