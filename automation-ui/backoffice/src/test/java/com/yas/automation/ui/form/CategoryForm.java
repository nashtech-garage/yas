package com.yas.automation.ui.form;

import com.yas.automation.base.form.BaseForm;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

@Getter
public class CategoryForm extends BaseForm {

    @FindBy(how = How.ID, using = "name")
    private WebElement name;

    @FindBy(how = How.ID, using = "slug")
    private WebElement slug;

    @FindBy(how = How.ID, using = "description")
    private WebElement description;

    @FindBy(how = How.ID, using = "metaKeywords")
    private WebElement metaKeywords;

    @FindBy(how = How.ID, using = "metaDescription")
    private WebElement metaDescription;

    @FindBy(how = How.ID, using = "isPublish")
    private WebElement isPublish;

    @FindBy(how = How.ID, using = "category-image")
    WebElement categoryImage;

    @FindBy(xpath = "//button[@type='submit' and contains(text(),'Save')]")
    WebElement saveBtn;

    public CategoryForm(WebDriver webDriver) {
        super(webDriver);
        PageFactory.initElements(webDriver, this);
    }

    @Override
    public WebElement getSubmitBtn() {
        return saveBtn;
    }
}
