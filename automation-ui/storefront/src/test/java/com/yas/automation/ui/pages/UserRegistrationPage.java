package com.yas.automation.ui.pages;

import static com.yas.automation.ui.util.WebElementUtil.getWebElementBy;

import com.yas.automation.ui.form.InputType;
import com.yas.automation.ui.form.UserRegisterForm;
import com.yas.automation.ui.hook.WebDriverFactory;
import com.yas.automation.ui.page.BasePage;
import com.yas.automation.ui.service.InputDelegateService;
import com.yas.automation.ui.util.WebElementUtil;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.How;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserRegistrationPage extends BasePage {
    private final WebDriverFactory webDriverFactory;
    private final InputDelegateService inputDelegateService;

    public UserRegistrationPage(WebDriverFactory webDriverFactory, InputDelegateService inputDelegateService) {
        super(webDriverFactory);
        this.webDriverFactory = webDriverFactory;
        this.inputDelegateService = inputDelegateService;
    }

    public void clickRegister() {
        WebElement registerBtn = getWebElementBy(webDriverFactory.getChromeDriver(), How.CLASS_NAME, "register");
        registerBtn.click();
    }

    public boolean existedErrorMessage() {
        return isElementPresent(
                () -> WebElementUtil.getWebElementBy(
                        webDriverFactory.getChromeDriver(),
                        How.ID,
                        "input-error-email"));
    }

    public void clickBackToLogin() {
        WebElement backToLogin = getWebElementBy(webDriverFactory.getChromeDriver(), How.XPATH, "//div[@id='kc-form-options']//a");
        backToLogin.click();
    }

    public void fillUserRegistrationData(UserRegisterForm userRegisterForm) {
        inputDelegateService.setInputValue(InputType.TEXT, userRegisterForm.getFirstName(), "firstName");
        inputDelegateService.setInputValue(InputType.TEXT, userRegisterForm.getLastName(), "lastName");
        inputDelegateService.setInputValue(InputType.TEXT, userRegisterForm.getEmail(), String.format("%s@gmail.com", UUID.randomUUID()));
        inputDelegateService.setInputValue(InputType.TEXT, userRegisterForm.getUsername(), UUID.randomUUID().toString());
        inputDelegateService.setInputValue(InputType.TEXT, userRegisterForm.getPassword(), "password");
        inputDelegateService.setInputValue(InputType.TEXT, userRegisterForm.getPasswordConfirm(), "password");
    }

    public void fillInvalidEmail(UserRegisterForm userRegisterForm) {
        inputDelegateService.setInputValue(InputType.TEXT, userRegisterForm.getEmail(), "email");
    }
}

