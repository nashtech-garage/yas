package com.yas.automation.ui.form;

import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

@Getter
public class UserRegisterForm extends BaseForm {

    @FindBy(how = How.ID, using = "firstName")
    private WebElement firstName;

    @FindBy(how = How.ID, using = "lastName")
    private WebElement lastName;

    @FindBy(how = How.ID, using = "email")
    private WebElement email;

    @FindBy(how = How.ID, using = "username")
    private WebElement username;

    @FindBy(how = How.ID, using = "password")
    private WebElement password;

    @FindBy(how = How.ID, using = "password-confirm")
    private WebElement passwordConfirm;

    @FindBy(xpath = "//input[@class='register-submit' and @value='Register']")
    private WebElement btnRegister;

    public UserRegisterForm(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    @Override
    public WebElement getSubmitBtn() {
        return btnRegister;
    }
}
