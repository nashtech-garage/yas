package com.yas.automation.ui.form;

import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Abstract base class for web forms, providing common form functionality.
 */
@Getter
public abstract class BaseForm {

    private WebDriver driver;

    private BaseForm() {}

    public BaseForm(WebDriver driver) {
        this.driver = driver;
    }

    public void submitForm() {
        getSubmitBtn().click();
    }

    public abstract WebElement getSubmitBtn();
}
