package com.yas.automation.ui.pages;

import static com.yas.automation.ui.util.WebElementUtil.getWebElementBy;

import com.yas.automation.ui.hook.WebDriverFactory;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.How;
import org.springframework.stereotype.Component;

@Component
public class LoginPage {
    private final WebDriverFactory webDriverFactory;

    public LoginPage(WebDriverFactory webDriverFactory) {
        this.webDriverFactory = webDriverFactory;
    }

    public void login(String username, String password) {
        WebElement usernameEle = getWebElementBy(webDriverFactory.getChromeDriver(), How.ID, "username");
        usernameEle.sendKeys(username);

        WebElement passwordEle = getWebElementBy(webDriverFactory.getChromeDriver(), How.ID, "password");
        passwordEle.sendKeys(password);
    }

    public void clickLogin() {
        WebElement loginBtn = getWebElementBy(webDriverFactory.getChromeDriver(), How.CLASS_NAME, "submit");
        loginBtn.click();
    }
}
